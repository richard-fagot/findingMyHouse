package models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import play.db.ebean.Model;

@SuppressWarnings("serial")
@Entity
public class Distance extends Model {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String origin;
	private String destination;
	private double distance;
	private double duration;
	private double lon;
	private double lat;

	public static Finder<Long, Distance> find = new Finder<Long, Distance>(Long.class, Distance.class);
	

}
