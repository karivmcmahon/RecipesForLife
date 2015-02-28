﻿using System;
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
	* Gets json from server to insert contributers into database
	**/
	public partial class WebForm11 : System.Web.UI.Page
	{
		protected void Page_Load(object sender, EventArgs e)
		{
			string jsonInput = new System.IO.StreamReader(Context.Request.InputStream, System.Text.Encoding.UTF8).ReadToEnd();
			if (jsonInput != null)
			{
				JavaScriptSerializer js = new JavaScriptSerializer();
				var contribs = js.Deserialize<List<Contributer>>(jsonInput);
				for (int i = 0; i < contribs.Count(); i++)
				{
					int id = 0;
					SqlConnection connn = new SqlConnection(System.Configuration.ConfigurationManager.ConnectionStrings["SQLDbConnection"].ConnectionString);
					SqlCommand select = new SqlCommand(" SELECT id FROM Cookbook WHERE uniqueid=@uniqueid", connn);
					select.Parameters.AddWithValue("@uniqueid", contribs[i].bookid);
					connn.Open();
					try
					{

						SqlDataReader rdr = select.ExecuteReader();
						if(rdr.HasRows)
						{
							while (rdr.Read())
							{
								// read a row, for example:
								id = rdr.GetInt32(0);
							}
						}
						rdr.Close();
						SqlCommand insert = new SqlCommand("INSERT INTO Contributers(Cookbookid,usersId,updateTime,changeTime, progress) VALUES(@bookid, @usersid, @updateTime, @changeTime, @progress)", connn);
						insert.Parameters.AddWithValue("@bookid", id);
						insert.Parameters.AddWithValue("@usersid", contribs[i].email);
						insert.Parameters.AddWithValue("@updateTime", contribs[i].updateTime);
						insert.Parameters.AddWithValue("@changeTime", contribs[i].changeTime);
						insert.Parameters.AddWithValue("@progress", contribs[i].progress);
						try
						{

							SqlDataReader rdr2= insert.ExecuteReader();
							rdr2.Close();
						}									  
						catch (Exception ex)
						{

							Response.Write("Error ");
							Response.Write(ex);
						}
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
		
		public class Contributer
		{
			public string bookid { get; set; }
			public string email { get; set; }
			public string updateTime { get; set; }
			public string changeTime { get; set; }
			public string progress { get; set; }
		}
	}
}
