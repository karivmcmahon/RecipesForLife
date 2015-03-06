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
	Class that updates cookbooks based on  JSON sent from app
	**/
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
					SqlConnection connection = new SqlConnection(System.Configuration.ConfigurationManager.ConnectionStrings["SQLDbConnection"].ConnectionString);
					SqlCommand updateCookbook = new SqlCommand("UPDATE Cookbook SET name=@name, description=@description, privacyOption=@privacyOption, changeTime=@changeTime, image=@image WHERE uniqueid=@uniqueid", connection);
					updateCookbook.Parameters.AddWithValue("@name", cookbook[i].name);
					updateCookbook.Parameters.AddWithValue("@description", cookbook[i].description);
					updateCookbook.Parameters.AddWithValue("@privacyOption", cookbook[i].privacyOption);
					updateCookbook.Parameters.AddWithValue("@changeTime", cookbook[i].changeTime);
					updateCookbook.Parameters.AddWithValue("@uniqueid", cookbook[i].uniqueid);
					byte[] image  = null;
					if(cookbook[i].image != "")
					{
						image = Convert.FromBase64String(cookbook[i].image);
					}
					updateCookbook.Parameters.AddWithValue("@image", image);
					connection.Open();
					try
					{

						SqlDataReader rdr= updateCookbook.ExecuteReader();
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
		
		//Stores cookbook info
		public class Cookbook
		{
			public string name { get; set; }
			public string description { get; set; }
			public string creator { get; set; }
			public string privacyOption { get; set; }
			public string updateTime { get; set; }
			public string changeTime { get; set; }
			public string uniqueid { get; set; }
			public string image { get; set; }

		}
	}
}

