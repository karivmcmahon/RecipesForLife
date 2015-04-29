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
	* Script creates a JSON of contributers to send to app
	*
	* By Kari McMahon
	**/
	public partial class WebForm12 : System.Web.UI.Page
	{
		string lastUpdated = "";
		string change = "";
		SqlConnection con = null;
		JavaScriptSerializer js = new JavaScriptSerializer();
		SqlCommand select = null;
		
		protected void Page_Load(object sender, EventArgs e)
		{
			js.MaxJsonLength = Int32.MaxValue;
			//Reads json
			string jsonInput = new System.IO.StreamReader(Context.Request.InputStream, System.Text.Encoding.UTF8).ReadToEnd();
			if (jsonInput != null)
			{
				try
				{
					//Deserializes json
					var time = js.Deserialize<List<Date2>>(jsonInput);
					lastUpdated = time[0].updateTime; //gets last updated time - insert time
					change = time[0].change; //gets last change time - update time
					
					//sets up connection
					con = new SqlConnection(System.Configuration.ConfigurationManager.ConnectionStrings["SQLDbConnection"].ConnectionString);
					
					//retrieves contributors
					selectContribs();
				}catch(Exception ex)
				{
					Response.Write("Error");
					Response.Write(ex);
				}
				
			}
		}
		
		/**
		* Select contributers for update json or insert json whether change is true
		*
		**/
		public void selectContribs()
		{
			if(change == "true")
			{
				select = new SqlCommand(" SELECT * FROM Contributers WHERE changeTime > @lastUpdated", con);
			}
			else
			{
				select = new SqlCommand(" SELECT * FROM Contributers WHERE updateTime > @lastUpdated", con);
			}
			
			select.Parameters.AddWithValue("@lastUpdated", lastUpdated);
			con.Open();
			Contributers contributers = new Contributers();
			contributers.Contributer = new List<Contributer>();
			var reader = select.ExecuteReader();
			
			//Gets contributer info to place in a json
			while (reader.Read())
			{
				Contributer contrib = new Contributer();
				contrib.email = (string)reader["usersId"];
				contrib.progress = (string)reader["progress"];
				selectContribBook(reader, contrib, contributers.Contributer);
				
			}
			con.Close();
			
			//Creates json of contributers, serializes and writes
			string json = js.Serialize(contributers);
			Response.Write(json);
		}
		
		/**
		* Selects cookbook unique id which is associated with contributer
		*
		**/
		public void selectContribBook(SqlDataReader reader, Contributer contrib, List<Contributer> contribs)
		{
			SqlCommand selectbook = new SqlCommand(" SELECT uniqueid FROM Cookbook WHERE id=@id", con);
			selectbook.Parameters.AddWithValue("@id", (Int32)reader["Cookbookid"]);
			var reader2 = selectbook.ExecuteReader();
			while(reader2.Read())
			{
				contrib.bookid = (string)reader2["uniqueid"];
			}
			contribs.Add(contrib);
		}
		
		/**
		* Class stores json from app containing dates as well as whether its a change or not
		*
		**/
		public class Date2
		{
			public string updateTime { get; set; }
			public string change { get; set; }
		}

		/**
		* Class creates a list of contributers used for json
		**/
		public class Contributers
		{
			public List<Contributer> Contributer { get; set;} 

		}

		/**
		* Class stores contrib info to send in JSON
		*
		**/
		public class Contributer
		{
			public string bookid { get; set; }
			public string email { get; set; }
			public string progress {get; set; }
		}

	}
}

