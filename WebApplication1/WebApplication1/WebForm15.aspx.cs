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

	/**
	* Script handles the sending of review json from database to app
	*
	* By Kari McMahon
	**/
	public partial class WebForm15 : System.Web.UI.Page
	{
		Int32 reviewID = 0;
		string jsonInput = "";
		string lastUpdated = "";
		SqlConnection connection1 = null;
		JavaScriptSerializer js = new JavaScriptSerializer();
		
		protected void Page_Load(object sender, EventArgs e)
		{
			js.MaxJsonLength = Int32.MaxValue;
			//Reads JSON
			jsonInput = new System.IO.StreamReader(Context.Request.InputStream, System.Text.Encoding.UTF8).ReadToEnd();
			if (jsonInput != null)
			{
				try
				{
					//Gets last update time
					var time = js.Deserialize<List<Date>>(jsonInput);
					lastUpdated = time[0].updateTime;
					connection1 = new SqlConnection(System.Configuration.ConfigurationManager.ConnectionStrings["SQLDbConnection"].ConnectionString);
					
					//Selects reviews based on this time
					SqlCommand selectreview = new SqlCommand(" SELECT * FROM Review WHERE updateTime > @lastUpdated", connection1);
					selectreview.Parameters.AddWithValue("@lastUpdated", lastUpdated);
					connection1.Open();
					Reviews reviews = new Reviews();
					var reviewReader = selectreview.ExecuteReader();
					reviews.Review = new List<Review>();
					
					while (reviewReader.Read())
					{
						//Create review object for json based on review
						Review review = new Review();			
						review.comment = (string)reviewReader["review"];
						review.user = (string)reviewReader["userid"];
						reviewID = (Int32)reviewReader["reviewId"];
						
						//Select unique id for recipe to place in json - the recipe id which review is connected too
						SqlCommand selectRecipeUniqueID = new SqlCommand("SELECT uniqueid FROM Recipe INNER JOIN ReviewRecipe ON ReviewRecipe.Recipeid = Recipe.id INNER JOIN Review ON Review.reviewId = ReviewRecipe.ReviewId WHERE Review.reviewId = @id", connection1);
						selectRecipeUniqueID.Parameters.AddWithValue("@id", reviewID);
						var selectRecipeReader = selectRecipeUniqueID.ExecuteReader();
	
						while(selectRecipeReader.Read())
						{
							review.recipeuniqueid = (string)selectRecipeReader["uniqueid"];
						}
						
						reviews.Review.Add(review); 
					}		
					connection1.Close();
					string json = js.Serialize(reviews); //serialize to json and write
					Response.Write(json);
				}
				catch(Exception ex)
				{
					Response.Write("Error");
					Response.Write(ex);
				}
			}
		}
		
		/**
		* Class which holds date sent from app
		*
		**/
		public class Date
		{
			public string updateTime { get; set; }
		}
		
		/**
		* Class creates a list of reviews for json
		**/
		public class Reviews
		{
			public List<Review> Review { get; set;} 

		}
		
		/**
		* Review class which stores json to send to app
		*
		**/
		public class Review
		{
			public string comment { get; set; }
			public string user { get; set; }
			public string recipeuniqueid { get; set; }

		}
	}
}