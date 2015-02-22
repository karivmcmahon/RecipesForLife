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
	public partial class WebForm7 : System.Web.UI.Page
	{
		protected void Page_Load(object sender, EventArgs e)
		{
			string jsonInput = new System.IO.StreamReader(Context.Request.InputStream, System.Text.Encoding.UTF8).ReadToEnd();
			if (jsonInput != null)
			{
				JavaScriptSerializer js = new JavaScriptSerializer();
				var cookbook = js.Deserialize<List<Cookbook>>(jsonInput);
				for (int i = 0; i < cookbook.Count(); i++)
				{
					SqlConnection connection = new SqlConnection(System.Configuration.ConfigurationManager.ConnectionStrings["SQLDbConnection"].ConnectionString);
					SqlCommand insertCookbook = new SqlCommand("INSERT INTO Cookbook(name,description,creator,updateTime,changeTime,uniqueid, privacyOption) VALUES (@name, @description, @creator, @updateTime, @changeTime, @uniqueid, @privacyOption)", connection);
					insertCookbook.Parameters.AddWithValue("@name", cookbook[i].name);
					insertCookbook.Parameters.AddWithValue("@description", cookbook[i].description);
					insertCookbook.Parameters.AddWithValue("@creator", cookbook[i].creator);
					insertCookbook.Parameters.AddWithValue("@updateTime", cookbook[i].updateTime);
					insertCookbook.Parameters.AddWithValue("@changeTime", cookbook[i].changeTime);
					insertCookbook.Parameters.AddWithValue("@uniqueid", cookbook[i].uniqueid);
					insertCookbook.Parameters.AddWithValue("@privacyOption", cookbook[i].privacyOption);
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
			}
		}
		
		public class Cookbook
		{
			public string name { get; set; }
			public string description { get; set; }
			public string creator { get; set; }
			public string privacyOption { get; set; }
			public string updateTime { get; set; }
			public string changeTime { get; set; }
			public string uniqueid { get; set; }

		}
		
	}
}

