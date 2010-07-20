package rest;
import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import org.apache.solr.analysis.LowerCaseFilterFactory;
import org.apache.solr.analysis.SnowballPorterFilterFactory;
import org.apache.solr.analysis.StandardTokenizerFactory;
import org.hibernate.search.annotations.*;

@Entity
@Table(name="mail")
@Indexed
@AnalyzerDef(name = "customanalyzer",
		tokenizer = @TokenizerDef(factory = StandardTokenizerFactory.class),
		filters = {
		@TokenFilterDef(factory = LowerCaseFilterFactory.class),
		@TokenFilterDef(factory = SnowballPorterFilterFactory.class, params = {
		@Parameter(name = "language", value = "English")
		})
		})
public class Mail implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String id;
	private String subject;
	private String author;
	private Date date;
	private String content;
	private String parentId;
	
	public Mail(){}
	public Mail(String subject,String author,Date date, String content){
		this.subject=subject;
		this.author=author;
		this.date=date;
		this.content=content;
	}
	
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	@Column(name="subject")
	@Field(index=Index.TOKENIZED, store=Store.NO)
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	
	@Column(name="author")
	@Field(index=Index.TOKENIZED, store=Store.NO)
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	
	@Column(name="date")
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	

	@Column(name="parentId")
	public String getParentId() {
		return parentId;
	}
	
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
	
	
	@Column(name = "content")
	@Field(index=Index.TOKENIZED, store=Store.NO)
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}

	public boolean equals(Object o){		
		if(o instanceof Mail){
			Mail other = (Mail) o;
			return this.getId().equals(other.getId());
		}
		else return false;
	}
	
	public int hashCode() {
		return this.hashCode();
	}	
}
