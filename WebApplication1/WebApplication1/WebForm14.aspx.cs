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
	* Script deserializes reviews JSON and handles inserts for reviews into the database
	*
	* By Kari McMahon
	**/
	public partial class WebForm14 : System.Web.UI.Page
	{
		Int32 reviewId = 0;
		Int32 recipeid = 0;
		SqlConnection connection;
		SqlTransaction transaction;
		
		protected void Page_Load(object sender, EventArgs e)
		{
			string jsonInput = new System.IO.StreamReader(Context.Request.InputStream, System.Text.Encoding.UTF8).ReadToEnd();
			if (jsonInput != null)
			{
				try
				{
					//Deserializing JSON
					JavaScriptSerializer js = new JavaScriptSerializer();
					js.MaxJsonLength = Int32.MaxValue;
					var review = js.Deserialize<List<Review>>(jsonInput);
					
					for (int i = 0; i < review.Count(); i++)
					{
					
						reviewId = 0;
						connection = new SqlConnection(System.Configuration.ConfigurationManager.ConnectionStrings["SQLDbConnection"].ConnectionString);
						connection.Open();
						transaction = connection.BeginTransaction(); //Start transaction
						
						try
						{
							
							insertReview( i, review);
							selectRecipeID(i, review);
							insertRecipeReviewLink(i, review);							
							//Commit the transactions
							transaction.Commit();		
						}
						catch(Exception ex)
						{
							//If error occurs rollback transaction
							transaction.Rollback();
							Response.Write("Error");
							Response.Write(ex);
						}
						connection.Close();
					}
					}
					catch(Exception ex)
						{
							//If error occurs rollback transaction
							transaction.Rollback();
							Response.Write("Error");
							Response.Write(ex);
						}
					
				}
			}
			
			
			/**
			* Inserts review into database
			*
			* i - point in loop
			* review - list of review info from json
			**/
			public void insertReview( int i, List<Review> review)
			{
				SqlCommand insertReview = new SqlCommand("INSERT INTO Review(review, userid, updateTime) OUTPUT INSERTED.reviewId VALUES (@review, @userid, @updateTime)", connection, transaction);
				insertReview.Parameters.AddWithValue("@review", review[i].comment);
				insertReview.Parameters.AddWithValue("@userid", review[i].user);
				insertReview.Parameters.AddWithValue("@updateTime", review[i].updateTime);
				
				
				try
				{
					try
					{
						//Inserts the review and retrieves id from insert
						reviewId = (Int32)insertReview.ExecuteScalar();	
					}
					catch (Exception ex)
					{
						Response.Write("Error reviewid ");
						Response.Write(ex);
						throw;
					}
				}
				catch(Exception ex)
				{
					Response.Write("Error");
					Response.Write(ex);
					throw;
				}	
				
			}
			
			/**
			* Select id from recipe based on uniqueid - the recipe which links to review
			*
			* i - point in loop
			* review - list of reviews from json
			**/
			public void selectRecipeID(int i, List<Review> review)
			{
				//Retrieve id from recipe based on unique id
				SqlCommand selectRecipeID = new SqlCommand(" SELECT id FROM Recipe WHERE uniqueid=@uniqueid", connection, transaction);
				selectRecipeID.Parameters.AddWithValue("@uniqueid", review[i].recipeuniqueid);
				try
				{

					SqlDataReader rdr = selectRecipeID.ExecuteReader();
					if(rdr.HasRows)
					{
						while (rdr.Read())
						{
							recipeid = rdr.GetInt32(0);
						}
					}
					rdr.Close();

				}
				catch (Exception ex)
				{

					Response.Write("Error at review recipe ");
					Response.Write(ex);
					throw;
				}
			}
			
			/**
			* Inserts link between recipe and review
			*
			* i - point in loop
			* review - list of review info from json
			**/
			public void insertRecipeReviewLink(int i, List<Review> review)
			{
				//Insert review and recipe links
				SqlCommand insertReviewLink = new SqlCommand(" INSERT INTO ReviewRecipe(ReviewId, Recipeid, updateTime) VALUES(@reviewid, @recipeid, @updateTime)", connection, transaction);
				insertReviewLink.Parameters.AddWithValue("@reviewid", reviewId);
				insertReviewLink.Parameters.AddWithValue("@recipeid", recipeid);
				insertReviewLink.Parameters.AddWithValue("@updateTime", review[i].updateTime);
				try
				{
					SqlDataReader rdr2 = insertReviewLink.ExecuteReader();
					rdr2.Close();

				}
				catch (Exception ex)
				{
					Response.Write("Error ");
					Response.Write(ex);
					throw;
				}
			}
			
			/**
			* Class stores review json
			*
			**/
			public class Review
			{
				public string comment { get; set; }
				public string user { get; set; }
				public string recipeuniqueid { get; set; }
				public string updateTime { get; set; }

			}
		}
	}
