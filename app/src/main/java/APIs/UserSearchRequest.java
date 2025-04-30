package APIs;

import com.google.gson.annotations.SerializedName;

public class UserSearchRequest {
    @SerializedName("query")
    private String query;

    public UserSearchRequest(String query) {
        this.query = query;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }
}