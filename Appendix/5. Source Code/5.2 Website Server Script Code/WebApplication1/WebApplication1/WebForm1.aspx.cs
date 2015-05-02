﻿using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.UI;
using System.IO;
using System.Web.UI.WebControls;
using System.Data.SqlClient;
using System.Configuration;
using System.Web.Script.Serialization;

namespace WebApplication1
{
	/**
	* Script handles account information received from JSON and inserts into db
	*
	* By Kari McMahon
	**/
	public partial class WebForm1 : System.Web.UI.Page
	{
		string jsonInput = "";
		SqlConnection con = null;
		SqlCommand insertUsers = null;
		Int32 newId = 0;
		List<Account> account  = new List<Account>();
		SqlTransaction transaction;
		
		protected void Page_Load(object sender, EventArgs e)
		{
			//Reads Json on page load
			jsonInput = new System.IO.StreamReader(Context.Request.InputStream, System.Text.Encoding.UTF8).ReadToEnd();
			
			if (jsonInput != null)
			{
				try
				{
					insertAccount();    
				}
				catch(Exception ex)
				{
					Response.Write("Error Account");
					Response.Write(ex);
				}
			}
			
		}

		/**
		* Inserts account information from json into database
		*
		**/
		public void insertAccount()
		{
			//Serializing the json into account list
			JavaScriptSerializer js = new JavaScriptSerializer();
			js.MaxJsonLength = Int32.MaxValue;
			account = js.Deserialize<List<Account>>(jsonInput);
			
			//Inserts json info into users table of database
			for (int i = 0; i < account.Count(); i++)
			{
				con = new SqlConnection(System.Configuration.ConfigurationManager.ConnectionStrings["SQLDbConnection"].ConnectionString);
				con.Open();
				transaction = con.BeginTransaction(); // begin transaction
				
				//Command to insert user info baased on json
				insertUsers = new SqlCommand(" INSERT INTO Users(name, cookingInterest, updateTime, bio, city, country) OUTPUT INSERTED.id VALUES (@name, @cookingInterest, @updateTime, @bio, @city, @country)", con, transaction);
				insertUsers.Parameters.AddWithValue("@name", account[i].name);
				insertUsers.Parameters.AddWithValue("@cookingInterest", account[i].cookingInterest);
				insertUsers.Parameters.AddWithValue("@updateTime", account[i].updateTime);
				insertUsers.Parameters.AddWithValue("@bio", account[i].bio);
				insertUsers.Parameters.AddWithValue("@city", account[i].city);
				insertUsers.Parameters.AddWithValue("@country", account[i].country); 

				try
				{                 
					
					newId = (Int32) insertUsers.ExecuteScalar(); //insert users and retrieve id
					insertIndAccount(i); //inserts users related account info
					transaction.Commit(); //if successful commit transaction

				}
				catch (Exception ex)
				{
					transaction.Rollback();
					Response.Write("Error");
					Response.Write(ex);
				}

				
				con.Close(); 
			}
		}
		
		/**
		* Inserts account data into account table
		* int i - point in loop
		**/
		public void insertIndAccount(int i)
		{
			SqlCommand insertAccount = new SqlCommand(" INSERT INTO Account(id,email,password,updateTime) VALUES (@id,@email,@password,@updateTime)", con, transaction);
			insertAccount.Parameters.AddWithValue("@id", newId);
			insertAccount.Parameters.AddWithValue("@email", account[i].email);
			insertAccount.Parameters.AddWithValue("@password", account[i].password);
			insertAccount.Parameters.AddWithValue("@updateTime", account[i].updateTime);
			insertAccount.ExecuteNonQuery();
		}
		
		/**
		* Class where deserialized JSON is placed
		*
		**/
		public class Account
		{
			public int  id { get; set; }
			public string name { get; set; }
			public string updateTime { get; set; }
			public string country { get; set; }
			public string city { get; set; }
			public string bio { get; set; }
			public string email { get; set; }
			public string password { get; set; }
			public string cookingInterest { get; set; }
		}
	}
}




