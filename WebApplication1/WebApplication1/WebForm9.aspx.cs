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
    public partial class WebForm9 : System.Web.UI.Page
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
				SqlConnection connn = new SqlConnection(System.Configuration.ConfigurationManager.ConnectionStrings["SQLDbConnection"].ConnectionString);
                SqlCommand insert = new SqlCommand("UPDATE Cookbook SET name=@name, description=@description, privacyOption=@privacyOption, changeTime=@changeTime WHERE uniqueid=@uniqueid", connn);
				insert.Parameters.AddWithValue("@name", cookbook[i].name);
                insert.Parameters.AddWithValue("@description", cookbook[i].description);
				insert.Parameters.AddWithValue("@privacyOption", cookbook[i].privacyOption);
				insert.Parameters.AddWithValue("@changeTime", cookbook[i].changeTime);
                insert.Parameters.AddWithValue("@uniqueid", cookbook[i].uniqueid);
				connn.Open();
                     try
                     {

                        SqlDataReader rdr= insert.ExecuteReader();
                                          rdr.Close();
					}
                     catch (Exception ex)
                     {

                         Response.Write("Error ");
                         Response.Write(ex);
                     }
                     connn.Close();	
					}
			}
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