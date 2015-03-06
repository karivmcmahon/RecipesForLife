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
	* Class retrieves JSON of cookbooks to be inserted into database
	**/
	public partial class WebForm7 : System.Web.UI.Page
	{
		protected void Page_Load(object sender, EventArgs e)
		{
			string jsonInput = new System.IO.StreamReader(Context.Request.InputStream, System.Text.Encoding.UTF8).ReadToEnd();
			if (jsonInput != null)
			{
				try
				{
					JavaScriptSerializer js = new JavaScriptSerializer();
					var cookbook = js.Deserialize<List<Cookbook>>(jsonInput);
					for (int i = 0; i < cookbook.Count(); i++)
					{
						SqlConnection connection = new SqlConnection(System.Configuration.ConfigurationManager.ConnectionStrings["SQLDbConnection"].ConnectionString);
						SqlCommand insertCookbook = new SqlCommand("INSERT INTO Cookbook(name,description,creator,updateTime,changeTime,uniqueid, privacyOption, image) VALUES (@name, @description, @creator, @updateTime, @changeTime, @uniqueid, @privacyOption, @image)", connection);
						insertCookbook.Parameters.AddWithValue("@name", cookbook[i].name);
						insertCookbook.Parameters.AddWithValue("@description", cookbook[i].description);
						insertCookbook.Parameters.AddWithValue("@creator", cookbook[i].creator);
						insertCookbook.Parameters.AddWithValue("@updateTime", cookbook[i].updateTime);
						insertCookbook.Parameters.AddWithValue("@changeTime", cookbook[i].changeTime);
						insertCookbook.Parameters.AddWithValue("@uniqueid", cookbook[i].uniqueid);
						insertCookbook.Parameters.AddWithValue("@privacyOption", cookbook[i].privacyOption);
						byte[] image  = null;
						if(cookbook[i].image != "")
						{
							image = Convert.FromBase64String(cookbook[i].image);
						}
						insertCookbook.Parameters.AddWithValue("@image", image );
						connection.Open();
						try
						{
							SqlDataReader rdr= insertCookbook.ExecuteReader();
							rdr.Close();
						}
						catch (Exception ex)
						{
							Response.Write("Error ");
							Response.Write(ex);
						}
						connection.Close();
					}
				}catch(Exception ex)
				{
					Response.Write("Error");
					Response.Write(ex);
				}
			}
		}
		
		//Stores json cookbook
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

		}
		
	}
}

