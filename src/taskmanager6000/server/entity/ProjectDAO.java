package taskmanager6000.server.entity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

@Entity
@Table(name="project")
public class ProjectDAO {
	private Long id;
	private String createDate;
	private String dueDate;
	private String resolutionDate;
	private String name;
	private Boolean done;
	private Boolean deleted;
	private Long version;
	
	private Set<TaskDAO> tasks = new HashSet<TaskDAO>(0);
	
	public ProjectDAO() {
		super();
	}
	
	public ProjectDAO(Long id) {
		super();
		this.id = id;
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

	@Column(name="name")
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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
	
	@OneToMany(cascade=CascadeType.PERSIST, fetch=FetchType.EAGER, mappedBy="project")
    public Set<TaskDAO> getTasks() {
        return this.tasks;
    }
    
    public void setTasks(Set<TaskDAO> tasks) {
        this.tasks = tasks;
    }
	/*
	return JSON style formatted string
	*/
	@Override
	public String toString() {
		return "{id: " + id
			+ ", name: " + name
			+ ", version: " + version
			+ ", create_date: " + createDate
			+ ", due_date: " + dueDate
			+ ", resolution_date: " + resolutionDate
			+ ", done: " + done
			+ ", deleted: " + deleted
			+ ", tasks: " + tasks + "}";
	}
}
