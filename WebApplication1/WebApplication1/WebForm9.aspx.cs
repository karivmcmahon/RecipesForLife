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
	* Script updates cookbooks based on  JSON sent from app
	*
	* By Kari McMahon
	**/
	public partial class WebForm9 : System.Web.UI.Page
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
				
				var cookbook = js.Deserialize<List<Cookbook>>(jsonInput); //Deserialize json into cookbook objects
				for (int i = 0; i < cookbook.Count(); i++)
				{
					SqlConnection connection = new SqlConnection(System.Configuration.ConfigurationManager.ConnectionStrings["SQLDbConnection"].ConnectionString);
					
					//Command to update cookbooks
					SqlCommand updateCookbook = new SqlCommand("UPDATE Cookbook SET name=@name, description=@description, privacyOption=@privacyOption, changeTime=@changeTime, image=@image,progress=@progress WHERE uniqueid=@uniqueid", connection);
				
					//Bind params
					updateCookbook.Parameters.AddWithValue("@name", cookbook[i].name);
					updateCookbook.Parameters.AddWithValue("@description", cookbook[i].description);
					updateCookbook.Parameters.AddWithValue("@privacyOption", cookbook[i].privacyOption);
					updateCookbook.Parameters.AddWithValue("@changeTime", cookbook[i].changeTime);
					updateCookbook.Parameters.AddWithValue("@uniqueid", cookbook[i].uniqueid);
					updateCookbook.Parameters.AddWithValue("@progress", cookbook[i].progress);
					byte[] image  = null;
					if(cookbook[i].image != "")
					{
						image = Convert.FromBase64String(cookbook[i].image); //convert to byte array
					}
					updateCookbook.Parameters.AddWithValue("@image", image);
					connection.Open();
					try
					{

						SqlDataReader rdr= updateCookbook.ExecuteReader(); //update cookbook
						rdr.Close();
					}
					catch (Exception ex)
					{

						Response.Write("Error Update Cookbook ");
						Response.Write(ex);
					}
					connection.Close();	
				}
				}catch(Exception ex)
				{
					Response.Write("Error Update Cookbook ");
					Response.Write(ex);
				}
			}
		}
		
		/**
		* Stores cookbook info
		**/
		public class Cookbook
		{
			public string name { get; set; }
			public string description { get; set; }
			public string creator { get; set; }
			public string privacyOption { get; set; }
			public string updateTime { get; set; }
			public string changeTime { get; set; }
			public string uniqueid { get; set; }
			public string image { get; set; }
			public string progress { get; set; }

		}
	}
}

