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
	public partial class WebForm12 : System.Web.UI.Page
	{
		string lastUpdated = "";
		string change = "";
		SqlConnection con = null;
		JavaScriptSerializer js = new JavaScriptSerializer();
		SqlCommand select = null;
		
		protected void Page_Load(object sender, EventArgs e)
		{
			string jsonInput = new System.IO.StreamReader(Context.Request.InputStream, System.Text.Encoding.UTF8).ReadToEnd();
			if (jsonInput != null)
			{
				var time = js.Deserialize<List<Date2>>(jsonInput);
				lastUpdated = time[0].updateTime;
				change = time[0].change;
				con = new SqlConnection(System.Configuration.ConfigurationManager.ConnectionStrings["SQLDbConnection"].ConnectionString);
				selectContribs();
				
			}
		}
		
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
			
			while (reader.Read())
			{
				Contributer contrib = new Contributer();
				contrib.email = (string)reader["usersId"];
				contrib.progress = (string)reader["progress"];
				selectContribBook(reader, contrib, contributers.Contributer);
				
			}
			con.Close();
			string json = js.Serialize(contributers);
			Response.Write(json);
		}
		
		public void selectContribBook(SqlDataReader reader, Contributer contrib, List<Contributer> contribs)
		{
			SqlCommand select2 = new SqlCommand(" SELECT uniqueid FROM Cookbook WHERE id=@id", con);
			select2.Parameters.AddWithValue("@id", (Int32)reader["Cookbookid"]);
			var reader2 = select2.ExecuteReader();
			while(reader2.Read())
			{
				contrib.bookid = (string)reader2["uniqueid"];
			}
			contribs.Add(contrib);
		}
		
		public class Date2
		{
			public string updateTime { get; set; }
			public string change { get; set; }
		}

		public class Contributers
		{
			public List<Contributer> Contributer { get; set;} 

		}


		public class Contributer
		{
			public string bookid { get; set; }
			public string email { get; set; }
			public string progress {get; set; }
		}

	}
}

