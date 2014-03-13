package org.jbpm.services.task.impl.model.xml;

import static org.jbpm.services.task.impl.model.xml.AbstractJaxbTaskObject.unsupported;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.kie.api.task.model.Group;
import org.kie.api.task.model.I18NText;
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.api.task.model.PeopleAssignments;
import org.kie.api.task.model.Task;
import org.kie.api.task.model.TaskData;
import org.kie.api.task.model.User;
import org.kie.internal.task.api.TaskModelProvider;
import org.kie.internal.task.api.model.Deadlines;
import org.kie.internal.task.api.model.Delegation;
import org.kie.internal.task.api.model.InternalI18NText;
import org.kie.internal.task.api.model.InternalOrganizationalEntity;
import org.kie.internal.task.api.model.InternalPeopleAssignments;
import org.kie.internal.task.api.model.InternalTask;
import org.kie.internal.task.api.model.InternalTaskData;
import org.kie.internal.task.api.model.SubTasksStrategy;

@XmlRootElement(name="task")
@XmlAccessorType(XmlAccessType.FIELD)
public class JaxbTask implements InternalTask {

    @XmlElement
    @XmlSchemaType(name="long")
    private Long id;
    
    @XmlElement
    @XmlSchemaType(name="long")
    private Integer priority;

    @XmlElement(name="task-type")
    @XmlSchemaType(name="int")
    private String taskType; 

    @XmlElement(name="name")
    private List<JaxbI18NText> names;
    
    @XmlElement(name="subject")
    private List<JaxbI18NText> subjects;
    
    @XmlElement(name="description")
    private List<JaxbI18NText> descriptions;
    
    @XmlElement(name="people-assignments")
    private JaxbPeopleAssignments peopleAssignments;
    
    @XmlElement
    private JaxbTaskData taskData;
    
    @XmlElement
    private JaxbDeadlines deadlines = new JaxbDeadlines();
    
    @XmlElement(name="form-name")
    @XmlSchemaType(name="String")
    private String formName;
    
    public JaxbTask() { 
        // Default constructor
    }
    
    public JaxbTask(Task task) { 
        initialize(task);
    }
    
    public void initialize(Task task) { 
        if( task == null ) { 
            return;
        }
        this.id = task.getId();
        this.priority = task.getPriority();
        this.peopleAssignments = new JaxbPeopleAssignments(task.getPeopleAssignments());

        // Done because we get a (lazy-initialized) entity back from the task service, which causes problems outside a tx
        // .. so we "eager-initialize" all values here to avoid problems later. (Also in JaxbPeopleAssignments)
        // Collection.toArray() == PersistenceBag.toArray(), which calls PersistenceBag.read(), initializing collection
        // See org.hibernate.collection.internal.PersistenceBag
        this.names = JaxbI18NText.convertListFromInterfaceToJaxbImpl(task.getNames());
        this.subjects = JaxbI18NText.convertListFromInterfaceToJaxbImpl(task.getSubjects());
        this.descriptions = JaxbI18NText.convertListFromInterfaceToJaxbImpl(task.getDescriptions());
        
        this.taskData = new JaxbTaskData(task.getTaskData());
        this.taskType = task.getTaskType();
        this.formName = ((InternalTask)task).getFormName();
    }
    
    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public void setId(long id) {
        this.id = id;
    }

    @Override
    public int getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    @Override
    public void setPriority(int priority) {
        this.priority = priority;
    }

    @Override
    public List<I18NText> getNames() {
        if( names == null ) { 
            names = Collections.emptyList();
        }
        return Collections.unmodifiableList(JaxbI18NText.convertListFromJaxbImplToInterface(names));
    }

    public void setNames(List<I18NText> names) {
        this.names = JaxbI18NText.convertListFromInterfaceToJaxbImpl(names);
    }

    @Override
    public List<I18NText> getSubjects() {
        if( subjects == null ) { 
            subjects = Collections.emptyList();
        }
        return Collections.unmodifiableList(JaxbI18NText.convertListFromJaxbImplToInterface(subjects));
    }

    public void setSubjects(List<I18NText> subjects) {
        this.subjects = JaxbI18NText.convertListFromInterfaceToJaxbImpl(subjects);
    }

    @Override
    public List<I18NText> getDescriptions() {
        if( descriptions == null ) { 
            descriptions = Collections.emptyList();
        }
        return Collections.unmodifiableList(JaxbI18NText.convertListFromJaxbImplToInterface(descriptions));
    }

    public void setDescriptions(List<I18NText> descriptions) {
        this.descriptions = JaxbI18NText.convertListFromInterfaceToJaxbImpl(descriptions);
    }

    @Override
    public PeopleAssignments getPeopleAssignments() {
        return peopleAssignments;
    }

    public void setPeopleAssignments(PeopleAssignments peopleAssignments) {
        if( peopleAssignments instanceof JaxbPeopleAssignments ) { 
        this.peopleAssignments = (JaxbPeopleAssignments) peopleAssignments;
        } else { 
            this.peopleAssignments = new JaxbPeopleAssignments(peopleAssignments);
        }
    }

    @Override
    public TaskData getTaskData() {
        return taskData;
    }

    public void setTaskData(TaskData taskData) {
        if( taskData instanceof JaxbTaskData ) { 
            this.taskData = (JaxbTaskData) taskData;
        } else { 
            this.taskData = new JaxbTaskData(taskData);
        }
    }

    @Override
    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    @Override
    public Deadlines getDeadlines() {
        return this.deadlines;
    }

    @Override
    public void setDeadlines(Deadlines deadlines) {
        // no-op
    }

    public Task getTask() { 
        Task taskImpl = TaskModelProvider.getFactory().newTask();
        List<I18NText> names = new ArrayList<I18NText>();
        for (I18NText n: this.getNames()) {
        	I18NText text = TaskModelProvider.getFactory().newI18NText();
        	((InternalI18NText) text).setLanguage(n.getLanguage());
        	((InternalI18NText) text).setText(n.getText());
            names.add(text);
        }
        ((InternalTask)taskImpl).setNames(names);
        List<I18NText> descriptions = new ArrayList<I18NText>();
        for (I18NText n: this.getDescriptions()) {
        	I18NText text = TaskModelProvider.getFactory().newI18NText();
        	((InternalI18NText) text).setLanguage(n.getLanguage());
        	((InternalI18NText) text).setText(n.getText());
            names.add(text);
        }
        ((InternalTask)taskImpl).setDescriptions(descriptions);
        List<I18NText> subjects = new ArrayList<I18NText>();
        for (I18NText n: this.getSubjects()) {
        	I18NText text = TaskModelProvider.getFactory().newI18NText();
        	((InternalI18NText) text).setLanguage(n.getLanguage());
        	((InternalI18NText) text).setText(n.getText());
            names.add(text);
        }
        ((InternalTask)taskImpl).setSubjects(subjects);
        ((InternalTask)taskImpl).setPriority(this.getPriority());
        InternalTaskData taskData = (InternalTaskData) TaskModelProvider.getFactory().newTaskData();
        taskData.setWorkItemId(this.getTaskData().getWorkItemId());
        taskData.setProcessInstanceId(this.getTaskData().getProcessInstanceId());
        taskData.setProcessId(this.getTaskData().getProcessId());
        taskData.setProcessSessionId(this.getTaskData().getProcessSessionId());
        taskData.setSkipable(this.getTaskData().isSkipable());
        PeopleAssignments peopleAssignments = TaskModelProvider.getFactory().newPeopleAssignments();
        List<OrganizationalEntity> potentialOwners = new ArrayList<OrganizationalEntity>();
        for (OrganizationalEntity e: this.getPeopleAssignments().getPotentialOwners()) {
            if (e instanceof User) {
            	User user = TaskModelProvider.getFactory().newUser();
            	((InternalOrganizationalEntity) user).setId(e.getId());
                potentialOwners.add(user);
            } else if (e instanceof Group) {
            	Group group = TaskModelProvider.getFactory().newGroup();
            	((InternalOrganizationalEntity) group).setId(e.getId());
                potentialOwners.add(group);
            }
        }
        ((InternalPeopleAssignments)peopleAssignments).setPotentialOwners(potentialOwners);
        List<OrganizationalEntity> businessAdmins = new ArrayList<OrganizationalEntity>();
        for (OrganizationalEntity e: this.getPeopleAssignments().getBusinessAdministrators()) {
            if (e instanceof User) {
            	User user = TaskModelProvider.getFactory().newUser();
            	((InternalOrganizationalEntity) user).setId(e.getId());
                businessAdmins.add(user);
            } else if (e instanceof Group) {
            	Group group = TaskModelProvider.getFactory().newGroup();
            	((InternalOrganizationalEntity) group).setId(e.getId());
                businessAdmins.add(group);
            }
        }
        if (this.getPeopleAssignments().getTaskInitiator() != null) {
        	User user = TaskModelProvider.getFactory().newUser();
        	((InternalOrganizationalEntity) user).setId(this.getPeopleAssignments().getTaskInitiator().getId());
        	((InternalPeopleAssignments)peopleAssignments).setTaskInitiator(user);
        }
        ((InternalPeopleAssignments)peopleAssignments).setBusinessAdministrators(businessAdmins);
        ((InternalPeopleAssignments)peopleAssignments).setExcludedOwners(new ArrayList<OrganizationalEntity>());
        ((InternalPeopleAssignments)peopleAssignments).setRecipients(new ArrayList<OrganizationalEntity>());
        ((InternalPeopleAssignments)peopleAssignments).setTaskStakeholders(new ArrayList<OrganizationalEntity>());
        ((InternalTask)taskImpl).setPeopleAssignments(peopleAssignments);        
        ((InternalTask)taskImpl).setTaskData(taskData);
       
        return taskImpl;
    }
    
    @Override
    public void setFormName(String formName) {
        this.formName = formName;
    }

    @Override
    public String getFormName() {
        return this.formName;
    }

    @Override
    public void readExternal(ObjectInput arg0) throws IOException, ClassNotFoundException {
        unsupported(Task.class);
    }

    @Override
    public void writeExternal(ObjectOutput arg0) throws IOException {
        unsupported(Task.class);
    }

    @Override
    public Boolean isArchived() {
        return (Boolean) unsupported(Task.class);
    }

    @Override
    public void setArchived(Boolean archived) {
        unsupported(Task.class);
    }

    public void setVersion(Integer version) {
        unsupported(Task.class);
    }

    @Override
    public int getVersion() {
        return (Integer) unsupported(Task.class);
    }

    @Override
    public Delegation getDelegation() {
        return (Delegation) unsupported(Task.class);
    }

    @Override
    public void setDelegation(Delegation delegation) {
        unsupported(Task.class);
    }

    @Override
    @JsonIgnore
    public Short getArchived() {
        return (Short) unsupported(Task.class);
    }

    @Override
    public SubTasksStrategy getSubTaskStrategy() {
        return (SubTasksStrategy) unsupported(Task.class);
    }

    @Override
    public void setSubTaskStrategy(SubTasksStrategy subTaskStrategy) {
        unsupported(Task.class);
    }

}
