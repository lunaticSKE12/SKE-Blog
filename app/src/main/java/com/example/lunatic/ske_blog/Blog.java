package com.example.lunatic.ske_blog;

/**
 * Blog class for Getter and Setter.
 * Created by Lunatic on 5/23/2017.
 */

public class Blog {

	private  String title;
	private String desc;
	private String image;

	public Blog(){

	}

	/**
	 * constructor
	 * @param title title of blog
	 * @param desc description of blog
	 * @param image image of blog
	 */
	public Blog(String title, String desc, String image) {
		this.title = title;
		this.desc = desc;
		this.image = image;
	}

	/**
	 * @return blog title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * set blog title
	 * @param title
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 *
	 * @return blog description
	 */
	public String getDesc() {
		return desc;
	}

	/**
	 * set blog description
	 * @param desc
	 */
	public void setDesc(String desc) {
		this.desc = desc;
	}

	/**
	 *
	 * @return blog image
	 */
	public String getImage() {
		return image;
	}

	/**
	 * set blog image
	 * @param image
	 */
	public void setImage(String image) {
		this.image = image;
	}
}
