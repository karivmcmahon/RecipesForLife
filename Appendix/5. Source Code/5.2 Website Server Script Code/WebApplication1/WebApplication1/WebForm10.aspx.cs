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
	* Script creates JSON of cookbooks to be updated after a certain date to send to app
	*
	* By Kari McMahon
	**/
	public partial class WebForm10 : System.Web.UI.Page
	{
		protected void Page_Load(object sender, EventArgs e)
		{
			//Reads json
			string jsonInput = new System.IO.StreamReader(Context.Request.InputStream, System.Text.Encoding.UTF8).ReadToEnd();
			if (jsonInput != null)
			{
				try
				{
				JavaScriptSerializer js = new JavaScriptSerializer();
				js.MaxJsonLength = Int32.MaxValue;
		
				//deserializes json for date
				var time = js.Deserialize<List<Date2>>(jsonInput);
				string lastUpdated = time[0].changeTime; //gets last updated time from json
				
				
				SqlConnection connection = new SqlConnection(System.Configuration.ConfigurationManager.ConnectionStrings["SQLDbConnection"].ConnectionString);
			
				//Command to get cookbooks from last updated time
				SqlCommand selectCookbook = new SqlCommand(" SELECT * FROM Cookbook WHERE changeTime > @lastUpdated", connection);
				selectCookbook.Parameters.AddWithValue("@lastUpdated", lastUpdated);
				connection.Open();
				Cookbooks cookbooks = new Cookbooks();
				cookbooks.Cookbook = new List<Cookbook>();
				var reader = selectCookbook.ExecuteReader();
				while (reader.Read())
				{
					//Creates cookbook based on info from server db
					Cookbook cookbook = new Cookbook();
					cookbook.name = (string)reader["name"];
					cookbook.description = (string)reader["description"];
					cookbook.creator = (string)reader["creator"];
					cookbook.uniqueid = (string)reader["uniqueid"];
					cookbook.privacyOption = (string)reader["privacyOption"];
					cookbook.progress = (string)reader["progress"];
					byte[] image = (byte[])reader["image"];
					cookbook.image = Convert.ToBase64String(image);
					cookbooks.Cookbook.Add(cookbook);
				} 
				connection.Close();
				
				//Serializes cookbooks and sends to app
				string json = js.Serialize(cookbooks);
				Response.Write(json);
				}catch(Exception ex)
				{
					Response.Write("Error Cookbook Update ");
					Response.Write(ex);
				}
			}
		}
		
		/**
		*	Class stores date from JSON
		**/
		public class Date2
		{
			public string changeTime { get; set; }
		}
	
		/**
		* Class used to create list of cookbooks  for json array
		**/
		public class Cookbooks
		{
			public List<Cookbook> Cookbook { get; set;} 

		}

		/**
		*	Class stores cookbook info for json
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

