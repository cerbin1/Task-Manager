import React, { useEffect, useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import axios from "axios";

function EditTask(props) {
  const [task, setTask] = useState()
  const [users, setUsers] = useState()
  const [priorities, setPriorities] = useState()
  const [files, setFiles] = useState([]);
  const [categories, setCategories] = useState()
  const [showWorklogs, setShowWorklogs] = useState()
  const [errors, setErrors] = useState();

  const apiUrl = 'http://localhost:8080/api/';

  const location = useLocation()
  const { id } = location.state

  const navigate = useNavigate();

  useEffect(() => {

    loadTaskDetails();

    fetch(apiUrl + 'users', {
      headers: {
        "Authorization": `Bearer ` + localStorage.getItem('token'),
      }
    })
      .then(response => {
        if (!response.ok) {
          throw new Error('Network response was not ok');
        }
        return response.json();
      })
      .then(data => {
        setUsers(data)
      })
      .catch(error => {
        alert(error)
      });

    fetch(apiUrl + 'priorities', {
      headers: {
        "Authorization": `Bearer ` + localStorage.getItem('token'),
      }
    })
      .then(response => {
        if (!response.ok) {
          throw new Error('Network response was not ok');
        }
        return response.json();
      })
      .then(data => {
        setPriorities(data)
      })
      .catch(error => {
        alert(error)
      });

    fetch(apiUrl + 'tasks/categories', {
      headers: {
        "Authorization": `Bearer ` + localStorage.getItem('token'),
      }
    })
      .then(response => {
        if (!response.ok) {
          throw new Error('Network response was not ok');
        }
        return response.json();
      })
      .then(data => {
        setCategories(data)
      })
      .catch(error => {
        alert(error)
      });

  }, []);

  function loadTaskDetails() {
    fetch(apiUrl + 'tasks/' + id, {
      headers: {
        "Authorization": `Bearer ` + localStorage.getItem('token'),
      }
    })
      .then(response => {
        if (!response.ok) {
          throw new Error('Network response was not ok');
        }
        return response.json();
      })
      .then(data => {
        setTask(data)
      })
      .catch(error => {
        alert(error)
      });
  }

  function validateValues() {
    let errors = {};
    if (task.name.length == 0) {
      errors.name = true;
    }
    if (task.deadline.length == 0) {
      errors.deadline = true;
    }
    if (task.category.length == 0 || task.category == 'NO_CATEGORY') {
      errors.category = true;
    }
    if (!task.subtasks.every((subtask) => subtask.name)) {
      errors.subtasks = true;
    }
    if (!task.labels.every((label) => label)) {
      errors.labels = true;
    }
    return errors;
  };

  function updateTask(event) {
    event.preventDefault();

    const errorValues = validateValues();
    setErrors(errorValues)
    if (Object.keys(errorValues).length !== 0) {
      return;
    }

    fetch(apiUrl + 'tasks/' + task.id, {
      method: 'PUT',
      body: JSON.stringify(task),
      headers: {
        "Content-type": "application/json; charset=UTF-8",
        "Authorization": `Bearer ` + localStorage.getItem('token'),
      }
    })
      .then(response => {
        if (!response.ok) {
          throw new Error('Network response was not ok');
        }
        return response.json();
      })
      .then(task => {
        if (files.length) {
          uploadFiles(task.id)
        }
        else {
          navigate('/list');
        }
      })
      .catch(error => {
        alert(error)
      });
  }

  function uploadFiles(taskId) {
    const formData = new FormData();
    [...files].forEach((file) => {
      formData.append('files', file, file.name)
    })

    const url = apiUrl + 'files/upload?taskId=' + taskId;
    const config = {
      headers: {
        'content-type': 'multipart/form-data',
        "Authorization": `Bearer ` + localStorage.getItem('token'),
      },
    };
    axios.post(url, formData, config)
      .then((response) => {
        navigate('/list');
      })
      .catch((error) => {
        console.error("Error uploading file: ", error);
      });
  }

  function handleChange(event) {
    setTask({
      ...task,
      [event.target.id]: event.target.value
    })
  }

  function handleAssigneeChange(event) {
    const findAssignee = users.find((user) => {
      return user.id == event.target.value
    });
    setTask(task => {
      return {
        ...task,
        assignee: findAssignee
      };
    });
  }

  function handlePriorityChange(event) {
    const findPriority = priorities.find((priority) => {
      return priority.id == event.target.value
    });
    setTask(task => {
      return {
        ...task,
        priority: findPriority
      };
    });
  }

  function handleCancelButton() {
    navigate('/list');
  }

  function handleSubtaskChange(index, event) {
    let subtasks = task.subtasks.slice();
    subtasks[index].name = event.target.value;

    setTask({
      ...task,
      subtasks: subtasks
    })
  }

  function handleRemoveSubtaskButton(index) {
    let subtasks = task.subtasks.slice();
    console.log(index)
    subtasks.splice(index, 1)

    setTask({
      ...task,
      subtasks: subtasks
    })
  }

  function handleAddSubtaskButton() {
    setTask({
      ...task,
      subtasks: [...task.subtasks, { name: '' }]
    })
  }

  function handleDeleteAllTaskFilesButton() {
    fetch(apiUrl + 'files/task/' + task.id, {
      method: 'DELETE',
      headers: {
        "Content-type": "application/json; charset=UTF-8",
        "Authorization": `Bearer ` + localStorage.getItem('token'),
      }
    })
      .then(response => {
        if (!response.ok) {
          throw new Error('Network response was not ok');
        }
        loadTaskDetails();
      })
      .catch(error => {
        alert(error)
      });
  }

  function handleCategoryChangeButton(event) {
    setTask({
      ...task,
      category: event.target.value
    })
  }

  function handleLabelChange(index, event) {
    let labels = task.labels.slice();
    labels[index] = event.target.value;

    setTask({
      ...task,
      labels: labels
    })
  }

  function handleLabelAddButton() {
    setTask({
      ...task,
      labels: [...task.labels, '']
    })
  }

  function handleRemoveLabelButton(index) {
    let labels = task.labels.slice();
    labels.splice(index, 1)

    setTask({
      ...task,
      labels: labels
    })
  }

  return <div className='container'>
    {task &&
      <form onSubmit={updateTask}>
        <div className="form-group row">
          <label htmlFor="name" className="col-sm-2 col-form-label">Name</label>
          <div className="col-sm-10">
            <input type="text" className="form-control" id="name" value={task.name} onChange={handleChange} />
            {errors && errors.name &&
              <div className="alert alert-danger" role="alert">
                You must enter name before submitting.
              </div>
            }
          </div>
        </div>
        <div className="form-group row">
          <label htmlFor="deadline" className="col-sm-2 col-form-label">Deadline</label>
          <div className="col-sm-10">
            <input className="form-control" id="deadline" type="datetime-local" value={task.deadline} onChange={handleChange} />
            {errors && errors.deadline &&
              <div className="alert alert-danger" role="alert">
                You must enter deadline before submitting.
              </div>
            }
          </div>
        </div>

        {users &&
          <div className="form-group row">
            <label htmlFor="prority" className="col-sm-2 col-form-label">Assignee</label>
            <div className="col-sm-10">
              <select className="form-select" name="assignee" defaultValue={task.assignee.id} onChange={handleAssigneeChange}>
                {users.map((user, index) => <option key={index} value={user.id}>{user.name}</option>)}
              </select>
            </div>
          </div>
        }

        {priorities &&
          <div className="form-group row">
            <label htmlFor="prority" className="col-sm-2 col-form-label">Priority</label>
            <div className="col-sm-10">
              <select className="form-select" name="priority" defaultValue={task.priority.id} onChange={handlePriorityChange}>
                {priorities.map((priority, index) => <option key={index} value={priority.id}>{priority.value}</option>)}
              </select>
            </div>
          </div>
        }

        <h1>Labels</h1>
        {task.labels && task.labels.map((label, index) => {
          return <div key={index} className="input-group mb-1">
            <input type="text" className="form-control" id="name" value={label} onChange={handleLabelChange.bind(this, index)} />
            <button type="button" className="btn btn-danger" onClick={() => handleRemoveLabelButton(index)}>Delete</button>
          </div>
        })}
        {errors && errors.labels &&
          <div className="alert alert-danger" role="alert">
            You must enter value for every label before submitting.
          </div>
        }
        <button type="button" className="btn btn-success" onClick={handleLabelAddButton}>Add label</button>

        <h1>Category</h1>
        {categories &&
          <div className="d-flex align-items-center justify-content-center">
            <div className="form-group col-md-3">
              <select className="form-select" name="category" onChange={handleCategoryChangeButton}>
                {categories.map((category, index) => <option key={index} value={category}>{category}</option>)}
              </select>
            </div>
            {errors && errors.category &&
              <div className="alert alert-danger" role="alert">
                You must change category before submitting.
              </div>
            }
          </div>
        }

        <h1>Subtasks</h1>
        <div className="d-flex align-items-center justify-content-center">
          <div className="form-group col-md-3">
            {task.subtasks.map((subtask, index) => {
              return <div key={index} className="input-group sm-3">
                <input className="form-control" type="text" style={{ textAlign: "center" }} value={subtask.name} onChange={handleSubtaskChange.bind(this, index)} />
                <button type="button" className="btn btn-danger" onClick={() => handleRemoveSubtaskButton(index)}>Delete</button>
              </div>
            })}
          </div>
        </div>
        {errors && errors.subtasks &&
          <div className="alert alert-danger" role="alert">
            You must enter name for every subtask before submitting.
          </div>
        }
        <button type="button" className="btn btn-success" onClick={handleAddSubtaskButton}>Add subtask</button>

        <h1>Files upload</h1>
        {task.taskFiles.length == 0 ?
          <input type="file" multiple onChange={e => setFiles(e.target.files)} /> :
          <div>
            File was uploaded. If you want to upload new files you need to delete all previous one first.
            <button type="button" className="btn btn-danger" onClick={handleDeleteAllTaskFilesButton}>Delete all files</button>
          </div>
        }

        <h1>Worklogs</h1>
        {showWorklogs ?
          <div className="list-group">
            {task.worklogs.length ?
              <table className="table">
                <thead>
                  <tr>
                    <th scope="col">Date</th>
                    <th scope="col">Minutes</th>
                    <th scope="col">Comment</th>
                  </tr>
                </thead>
                <tbody>
                  {task.worklogs.map((worklog, i) => (
                    <tr key={i}>
                      <td>{worklog.date}</td>
                      <td>{worklog.minutes}</td>
                      <td>{worklog.comment}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
              : <b>No data.</b>}
          </div>
          :
          <button type="button" className="btn btn-primary" onClick={() => setShowWorklogs(true)}>Show worklogs</button>
        }

        <div className='form-control'>
          <button type="button" className="btn btn-secondary" onClick={handleCancelButton}>Cancel</button>
          <button type="submit" className="btn btn-primary">Update</button>
        </div>
      </form>
    }
  </div>
}

export default EditTask;
