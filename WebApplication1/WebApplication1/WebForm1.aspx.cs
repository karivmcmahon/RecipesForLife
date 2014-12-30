using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.UI;
using System.IO;
using System.Web.UI.WebControls;
using System.Data.SqlClient;
using System.Configuration;
using System.Runtime.Serialization.Json;
using System.Runtime.Serialization;
using System.Web.Script.Serialization;

namespace WebApplication1
{
    public partial class WebForm1 : System.Web.UI.Page
    {
        protected void Page_Load(object sender, EventArgs e)
        {
            string jsonInput = new System.IO.StreamReader(Context.Request.InputStream, System.Text.Encoding.UTF8).ReadToEnd();
            if (jsonInput != null)
            {
                int n;
                JavaScriptSerializer js = new JavaScriptSerializer();
                Person p = new Person();
                p.name = "John";
                p.age = 42;
                MemoryStream stream1 = new MemoryStream();
                DataContractJsonSerializer ser = new DataContractJsonSerializer(typeof(Person));
                //DataContractJsonSerializer ser2 = new DataContractJsonSerializer(typeof(Students));
                ser.WriteObject(stream1, p);
              

               var p2 = js.Deserialize<List<Student>>(jsonInput);
               for (int i = 0; i < p2.Count(); i++)
               {
                   Response.Write(p2[i].name);
                   Response.Write(p2[i].id);
               }
             //   Response.Write(p2[1].name);
            }
            

            // Encode image - String base64String = Convert.ToBase64String(stream1.ToArray());
            //p.image = base64String
        }

     /**   protected void Button1_Click(object sender, EventArgs e)
        {
            string connectionString = ConfigurationManager.ConnectionStrings["SQLDbConnection"].ToString();
            SqlConnection connection = new SqlConnection(connectionString);
            connection.Open();
            Label1.Text = "Connected to Database Server !!";
            connection.Close();
        } **/
    }
}
[DataContract]
internal class Person
{
    [DataMember]
    internal string name;

    [DataMember]
    internal int age;
}

public class Student
{
    public string  id { get; set; }
    public string name { get; set; }
}

public class Studentss
{
    public List<Student> Students { get; set; }
}
