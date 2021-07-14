package skaro.pokeflex.objects.move;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "url",
    "generation",
    "language"
})
public class Image 
{
    @JsonProperty("url")
    private String url;
    @JsonProperty("generation")
    private int generation;
    @JsonProperty("language")
    private String language;
    
    @JsonProperty("url")
	public void setUrl(String url) {
		this.url = url;
	}
    
    @JsonProperty("generation")
	public void setGeneration(int generation) {
		this.generation = generation;
	}
    
    @JsonProperty("language")
	public void setLanguage(String language) {
		this.language = language;
	}
	
    @JsonProperty("url")
	public String getUrl() {
		return url;
	}
    
    @JsonProperty("generation")
	public int getGeneration() {
		return generation;
	}
    
    @JsonProperty("language")
	public String getLanguage() {
		return language;
	}

	
    
}
