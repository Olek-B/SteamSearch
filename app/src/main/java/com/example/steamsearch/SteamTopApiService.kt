import com.example.steamsearch.MostPlayedGamesResponse
import retrofit2.http.GET

interface SteamTopApiService {
    @GET("ISteamChartsService/GetMostPlayedGames/v1/")
    suspend fun getMostPlayedGames(): MostPlayedGamesResponse
}
