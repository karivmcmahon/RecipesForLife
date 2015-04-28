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
	* Script creates JSON of cookbooks to be sent to app based on certain date
	*
	* By Kari McMahon
	**/
	public partial class WebForm8 : System.Web.UI.Page
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
					var time = js.Deserialize<List<Date2>>(jsonInput);
					string lastUpdated = time[0].updateTime; //gets last updated time from json
					
					//Sets up connection
					SqlConnection con = new SqlConnection(System.Configuration.ConfigurationManager.ConnectionStrings["SQLDbConnection"].ConnectionString);
					
					//Selects cookbook info from last updated
					SqlCommand select = new SqlCommand(" SELECT * FROM Cookbook WHERE updateTime > @lastUpdated", con);
					select.Parameters.AddWithValue("@lastUpdated", lastUpdated);
					con.Open();
					
					//Sets up vars
					Cookbooks cookbooks = new Cookbooks();
					cookbooks.Cookbook = new List<Cookbook>();
					var reader = select.ExecuteReader();
					while (reader.Read())
					{
						//Creates new cookbook object to use for json list
						byte[] image = new byte[0];
						Cookbook cookbook = new Cookbook();
						cookbook.name = (string)reader["name"];
						cookbook.description = (string)reader["description"];
						cookbook.creator = (string)reader["creator"];
						cookbook.uniqueid = (string)reader["uniqueid"];
						cookbook.privacyOption = (string)reader["privacyOption"];
						cookbook.progress = (string)reader["progress"];
						image = (byte[])reader["image"];
						cookbook.image = Convert.ToBase64String(image);
						cookbooks.Cookbook.Add(cookbook);
					} 
					con.Close();
					string json = js.Serialize(cookbooks); //serialize list into JSON and then writes it
					Response.Write(json);
				}catch(Exception ex)
				{
					Response.Write("Error Selecting Cookbooks");
					Response.Write(ex);
				}
			}
		}
		
		/**
		* Class stores date sent from app in json form
		**/
		public class Date2
		{
			public string updateTime { get; set; }
		}
		/**
		* Class stores list of cookbooks - for json array
		**/
		public class Cookbooks
		{
			public List<Cookbook> Cookbook { get; set;} 

		}
		/**
		* Class stores a cookbook
		**/
		public class Cookbook
		{
			public string name { get; set; }
			public string description { get; set; }
			public string creator { get; set; }
			public string privacyOption { get; set; }
			public string uniqueid { get; set; }
			public string image { get; set; }
			public string progress { get; set; }

		}
	}
}

