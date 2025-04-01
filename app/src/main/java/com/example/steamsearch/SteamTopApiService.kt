import com.example.steamsearch.MostPlayedGamesResponse
import retrofit2.http.GET

interface SteamTopApiService {
    @GET("ISteamChartsService/GetMostPlayedGames/v1/?key=748B30BDE8CEA2FEA3BF55A5EC086B13")
    suspend fun getMostPlayedGames(): MostPlayedGamesResponse
}
