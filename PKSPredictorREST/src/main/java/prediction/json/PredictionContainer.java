
package prediction.json;

import java.util.List;

public class PredictionContainer{
   	private Configuration configuration;
   	private List<FeaturesArray> featuresArray;
   	private Legend legend;
   	private String segment;
	private String querySequence;

 	public Configuration getConfiguration(){
		return this.configuration;
	}
	public void setConfiguration(Configuration configuration){
		this.configuration = configuration;
	}
 	public List<FeaturesArray> getFeaturesArray(){
		return this.featuresArray;
	}
	public void setFeaturesArray(List<FeaturesArray> featuresArray){
		this.featuresArray = featuresArray;
	}
 	public Legend getLegend(){
		return this.legend;
	}
	public void setLegend(Legend legend){
		this.legend = legend;
	}
 	public String getSegment(){
		return this.segment;
	}
	public void setSegment(String segment){
		this.segment = segment;
	}

	public String getQuerySequence() {
		return querySequence;
	}

	public void setQuerySequence(String querySequence) {
		this.querySequence = querySequence;
	}
}
