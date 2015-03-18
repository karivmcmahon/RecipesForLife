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
	public partial class WebForm14 : System.Web.UI.Page
	{
		Int32 reviewId = 0;
		Int32 recipeid = 0;
		protected void Page_Load(object sender, EventArgs e)
		{
			string jsonInput = new System.IO.StreamReader(Context.Request.InputStream, System.Text.Encoding.UTF8).ReadToEnd();
			if (jsonInput != null)
			{
				try
				{
					JavaScriptSerializer js = new JavaScriptSerializer();
					var review = js.Deserialize<List<Review>>(jsonInput);
					for (int i = 0; i < review.Count(); i++)
					{
						SqlConnection connection = new SqlConnection(System.Configuration.ConfigurationManager.ConnectionStrings["SQLDbConnection"].ConnectionString);
						SqlCommand insertReview = new SqlCommand("INSERT INTO Review(review, userid, updateTime) OUTPUT INSERTED.reviewId VALUES (@review, @userid, @updateTime)", connection);
						insertReview.Parameters.AddWithValue("@review", review[i].comment);
						insertReview.Parameters.AddWithValue("@userid", review[i].user);
						insertReview.Parameters.AddWithValue("@updateTime", review[i].updateTime);
						
						connection.Open();
						try
						{
							reviewId = (Int32)insertReview.ExecuteScalar();
							Response.Write("REVIEW ID " + reviewId);
						
						}
						catch (Exception ex)
						{
							Response.Write("Error reviewid ");
							Response.Write(ex);
						}
						
						
						SqlCommand selectRecipeID = new SqlCommand(" SELECT id FROM Recipe WHERE uniqueid=@uniqueid", connection);
						selectRecipeID.Parameters.AddWithValue("@uniqueid", review[i].recipeuniqueid);
						try
						{

							SqlDataReader rdr = selectRecipeID.ExecuteReader();
							if(rdr.HasRows)
							{
								while (rdr.Read())
								{
									// read a row, for example:
									recipeid = rdr.GetInt32(0);
									Response.Write("RECIPE ID " + recipeid);
								}
							}
							rdr.Close();

						}
						catch (Exception ex)
						{

							Response.Write("Error at recipe ");
							Response.Write(ex);
						}
						
				   SqlCommand insertReviewLink = new SqlCommand(" INSERT INTO ReviewRecipe(ReviewId, Recipeid, updateTime) VALUES(@reviewid, @recipeid, @updateTime)", connection);
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
					}
						
						
						connection.Close();
					}
				}catch(Exception ex)
				{
					Response.Write("Error");
					Response.Write(ex);
				}
			}
		}
		public class Review
		{
			public string comment { get; set; }
			public string user { get; set; }
			public string recipeuniqueid { get; set; }
			public string updateTime { get; set; }

		}
	}
}
