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
    public partial class WebForm10 : System.Web.UI.Page
    {
        protected void Page_Load(object sender, EventArgs e)
        {
			string jsonInput = new System.IO.StreamReader(Context.Request.InputStream, System.Text.Encoding.UTF8).ReadToEnd();
            if (jsonInput != null)
            {
                JavaScriptSerializer js = new JavaScriptSerializer();
                var time = js.Deserialize<List<Date2>>(jsonInput);
				string lastUpdated = time[0].changeTime;
					SqlConnection con = new SqlConnection(System.Configuration.ConfigurationManager.ConnectionStrings["SQLDbConnection"].ConnectionString);
				SqlCommand select = new SqlCommand(" SELECT * FROM Cookbook WHERE changeTime > @lastUpdated", con);
                select.Parameters.AddWithValue("@lastUpdated", lastUpdated);
				con.Open();
				Cookbooks cookbooks = new Cookbooks();
				cookbooks.Cookbook = new List<Cookbook>();
				var reader = select.ExecuteReader();
				while (reader.Read())
                {
					Cookbook cookbook = new Cookbook();
					cookbook.name = (string)reader["name"];
					cookbook.description = (string)reader["description"];
					cookbook.creator = (string)reader["creator"];
					cookbook.uniqueid = (string)reader["uniqueid"];
					cookbook.privacyOption = (string)reader["privacyOption"];
					cookbooks.Cookbook.Add(cookbook);
				} 
				con.Close();
				string json = js.Serialize(cookbooks);
                Response.Write(json);
			}
        }
		
		public class Date2
{
        public string changeTime { get; set; }
 }
 
 public class Cookbooks
{
    public List<Cookbook> Cookbook { get; set;} 

}

public class Cookbook
{
    public string name { get; set; }
    public string description { get; set; }
    public string creator { get; set; }
    public string privacyOption { get; set; }
    public string uniqueid { get; set; }

}
    }
}

