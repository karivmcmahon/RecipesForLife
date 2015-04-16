using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.UI;
using System.Web.UI.WebControls;
using System.Web.Script.Serialization;
using System.Data.SqlClient;

namespace WebApplication1
{
	/**
	Class updates contributer information based on JSON from app
	**/
	public partial class WebForm13 : System.Web.UI.Page
	{
		protected void Page_Load(object sender, EventArgs e)
		{
			string jsonInput = new System.IO.StreamReader(Context.Request.InputStream, System.Text.Encoding.UTF8).ReadToEnd();
			if (jsonInput != null)
			{
				try
				{
				JavaScriptSerializer js = new JavaScriptSerializer();
				js.MaxJsonLength = Int32.MaxValue;
				var contribs = js.Deserialize<List<Contributer>>(jsonInput); //deserializes json into contrib objects
				for (int i = 0; i < contribs.Count(); i++)
				{
					int id = 0;
					
					//Select id from cookbook based of uniqueid from json
					SqlConnection connn = new SqlConnection(System.Configuration.ConfigurationManager.ConnectionStrings["SQLDbConnection"].ConnectionString);
					SqlCommand select = new SqlCommand(" SELECT id FROM Cookbook WHERE uniqueid=@uniqueid", connn);
					select.Parameters.AddWithValue("@uniqueid", contribs[i].bookid);
					connn.Open();
					try
					{

						SqlDataReader rdr = select.ExecuteReader();
						if(rdr.HasRows)
						{
							while (rdr.Read())
							{
								id = rdr.GetInt32(0);
							}
						}
						rdr.Close();
						
						//Update contrib info in database 
						SqlCommand update = new SqlCommand("UPDATE Contributers SET progress=@progress, changeTime=@changeTime WHERE Cookbookid=@bookid AND usersId=@usersid", connn);
						update.Parameters.AddWithValue("@bookid", id);
						update.Parameters.AddWithValue("@usersid", contribs[i].email);
						update.Parameters.AddWithValue("@changeTime", contribs[i].changeTime);
						update.Parameters.AddWithValue("@progress", contribs[i].progress);
						try
						{

							SqlDataReader rdr2= update.ExecuteReader();
							rdr2.Close();
						}									  
						catch (Exception ex)
						{

							Response.Write("Error Contrib Update ");
							Response.Write(ex);
						}
					}
					catch (Exception ex)
					{

						Response.Write("Error Contrib Update ");
						Response.Write(ex);
					}
					connn.Close();
				}
				}catch(Exception ex)
				{
					Response.Write("Error Contrib Update");
					Response.Write(ex);
				}
			}
		}
		
		/**
		* Class stores info from contrib json
		*
		**/
		public class Contributer
		{
			public string bookid { get; set; }
			public string email { get; set; }
			public string updateTime { get; set; }
			public string changeTime { get; set; }
			public string progress { get; set; }
		}
	}
}

