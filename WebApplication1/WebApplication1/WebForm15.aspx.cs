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
			jsonInput = new System.IO.StreamReader(Context.Request.InputStream, System.Text.Encoding.UTF8).ReadToEnd();
			if (jsonInput != null)
			{
				try
				{
					//Gets last update
					var time = js.Deserialize<List<Date>>(jsonInput);
					lastUpdated = time[0].updateTime;
					connection1 = new SqlConnection(System.Configuration.ConfigurationManager.ConnectionStrings["SQLDbConnection"].ConnectionString);
					SqlCommand selectreview = new SqlCommand(" SELECT * FROM Review WHERE updateTime > @lastUpdated", connection1);
					selectreview.Parameters.AddWithValue("@lastUpdated", lastUpdated);
					connection1.Open();
					Reviews reviews = new Reviews();
					var reviewReader = selectreview.ExecuteReader();
					reviews.Review = new List<Review>();
					
					while (reviewReader.Read())
					{
						Review review = new Review();			
						review.comment = (string)reviewReader["review"];
						review.user = (string)reviewReader["userid"];
						reviewID = (Int32)reviewReader["reviewId"];
						
						
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
					string json = js.Serialize(reviews);
					Response.Write(json);
				}
				catch(Exception ex)
				{
					Response.Write("Error");
					Response.Write(ex);
				}
			}
		}
		
		public class Date
		{
			public string updateTime { get; set; }
		}
		
		public class Reviews
		{
			public List<Review> Review { get; set;} 

		}
		
		public class Review
		{
			public string comment { get; set; }
			public string user { get; set; }
			public string recipeuniqueid { get; set; }

		}
	}
}