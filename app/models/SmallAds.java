package models;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import play.db.ebean.Model;

@SuppressWarnings("serial")
@Entity
public class SmallAds extends Model {

	String url;
	
	@ManyToOne
	Distance distance;
	
	@Transient
	private Date date;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Distance getDistance() {
		return distance;
	}

	public void setDistance(Distance distance) {
		this.distance = distance;
	}
	
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}


	public static Finder<Long, SmallAds> find = new Finder<Long, SmallAds>(Long.class, SmallAds.class);
	
}
