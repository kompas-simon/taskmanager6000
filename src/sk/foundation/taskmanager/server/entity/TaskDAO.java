package sk.foundation.taskmanager.server.entity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

@Entity
@Table(name="task")
public class TaskDAO implements java.io.Serializable {

	private Long id;
	private String createDate;
	private String dueDate;
	private String resolutionDate;
	private String desc;
	private Boolean done;
	private Boolean deleted;
	private Long version;
	
	private ProjectDAO project;
	
	public TaskDAO() {
		super();
	}
	
	public TaskDAO(Long id) {
		super();
		this.id = id;
	}
	
	public TaskDAO(ProjectDAO project) {
		super();
		this.project = project;
	}

    @Id
    @Column(name="id", unique=true, nullable=false)
    @GeneratedValue(strategy=GenerationType.IDENTITY)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name="create_date")
	public String getCreateDate() {
		return createDate;
	}
	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

	@Column(name="due_date")
	public String getDueDate() {
		return dueDate;
	}
	public void setDueDate(String dueDate) {
		this.dueDate = dueDate;
	}

	@Column(name="resolution_date")
	public String getResolutionDate() {
		return resolutionDate;
	}
	public void setResolutionDate(String resolutionDate) {
		this.resolutionDate = resolutionDate;
	}

	@Column(name="desc")
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}

	@Column(name="done")		
	public Boolean getDone() {
		return done;
	}

	public void setDone(Boolean done) {
		this.done = done;
	}

	@Column(name="deleted")
	public Boolean getDeleted() {
		return deleted;
	}

	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}

	@Version
	@Column(name="version")
	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="project_id")
    public ProjectDAO getProject() {
        return this.project;
    }
    
    public void setProject(ProjectDAO project) {
        this.project = project;
    }
	
	/*
	return JSON style formatted string
	*/
	@Override
	public String toString() {
		return "{id: " + id
			+ ", project_id: " + (project != null ? project.getId() : null)
			+ ", desc: " + desc
			+ ", version: " + version
			+ ", create_date: " + createDate
			+ ", due_date: " + dueDate
			+ ", resolution_date: " + resolutionDate
			+ ", done: " + done
			+ ", deleted: " + deleted + "}\n";
	}
}
