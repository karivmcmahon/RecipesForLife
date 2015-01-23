using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.UI;
using System.Web.UI.WebControls;
using System.Web.Script.Serialization;
using System.Data.SqlClient;
using System.Configuration;

namespace WebApplication1
{
    public partial class WebForm2 : System.Web.UI.Page
    {
        protected void Page_Load(object sender, EventArgs e)
        {
            Response.Clear();
            string jsonInput = new System.IO.StreamReader(Context.Request.InputStream, System.Text.Encoding.UTF8).ReadToEnd();
            if (jsonInput != null)
            {

                //Serializing a json
                JavaScriptSerializer js = new JavaScriptSerializer();
                /**  Employee emp = new Employee();
                  emp.Name = "a";
                  emp.age = 12;
                  string json = js.Serialize(emp); **/

                var p2 = js.Deserialize<List<Date>>(jsonInput);
                string lastUpdated = p2[0].updateTime;
                SqlConnection con = new SqlConnection(System.Configuration.ConfigurationManager.ConnectionStrings["SQLDbConnection"].ConnectionString);
                SqlConnection con2 = new SqlConnection(System.Configuration.ConfigurationManager.ConnectionStrings["SQLDbConnection"].ConnectionString);
                SqlCommand select = new SqlCommand(" SELECT * FROM Account WHERE updateTime > @lastUpdated", con);
                SqlCommand select2 = new SqlCommand(" SELECT * FROM Users WHERE updateTime > @lastUpdated", con2);
                select.Parameters.AddWithValue("@lastUpdated", lastUpdated);
                select2.Parameters.AddWithValue("@lastUpdated", lastUpdated);
                con.Open();
                con2.Open();
                var reader = select.ExecuteReader();
                
                var list = new List<Acc>();
                var list2 = new List<User>();
                
                int count = reader.FieldCount;
                var reader2 = select2.ExecuteReader();
                while (reader.Read())
                {
                    Acc account = new Acc();
                    account.email = (string)reader["email"];
                    account.password = (string)reader["password"];
                    
                    while (reader2.Read())
                    {

                        User user = new User();
                        user.name = (string)reader2["name"];
                        user.bio = (string)reader2["bio"];
                        user.city = (string)reader2["city"];
                        user.country = (string)reader2["country"];
                        user.cookingInterest = (string)reader2["cookingInterest"];
                        list2.Add(user);
                        
                       
                    }
                    list.Add(account);
                 
                }
                var fulllist = new List<Account>();
                for (int i = 0; i < list.Count; i++ )
                {
                    Account account = new Account();
                    account.email = list[i].email;
                    account.password = list[i].password;
                    account.city = list2[i].city;
                    account.country = list2[i].country;
                    account.bio = list2[i].bio;
                    account.cookingInterest = list2[i].cookingInterest;
                    account.name = list2[i].name;
                    fulllist.Add(account);

                }
                    con2.Close();
                con.Close();
               
                string json = js.Serialize(fulllist);
				list.Clear();
                Response.Write(json);
			    json = "";
            }
        }
    }

    public class Date
    {
        public string updateTime { get; set; }
    }
    public class Account
    {
        public int id { get; set; }
        public string name { get; set; }
        public string updateTime { get; set; }
        public string country { get; set; }
        public string city { get; set; }
        public string bio { get; set; }
        public string email { get; set; }
        public string password { get; set; }
        public string cookingInterest { get; set; }
    }

    public class Acc
    {
        public string email { get; set; }
        public string password { get; set; }


    }

    public class User
    {
        public string name { get; set; }
        public string updateTime { get; set; }
        public string country { get; set; }
        public string city { get; set; }
        public string bio { get; set; }
        public string cookingInterest { get; set; }
    }

}

