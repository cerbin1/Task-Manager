import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom'


function Notifications(props) {
  const [data, setData] = useState([])

  const apiUrl = 'http://localhost:8080/api/notifications';

  useEffect(() => {
    loadNotifications();
  }, []);

  function loadNotifications() {
    fetch(apiUrl, {
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
        setData(data);
      })
      .catch(error => {
        alert(error);
      });
  }

  function handleRemove(id) {
    fetch(apiUrl + '/' + id, {
      method: 'DELETE',
      headers: {
        "Authorization": `Bearer ` + localStorage.getItem('token'),
      }
    })
      .then(response => {
        if (!response.ok) {
          throw new Error('Network response was not ok');
        }
        loadNotifications();
      })
      .catch(error => {
        alert(error)
      });
  }

  function handleMarkNotificationAsRead(id) {
    fetch(apiUrl + '/' + id + '/read', {
      method: 'PUT',
      headers: {
        "Authorization": `Bearer ` + localStorage.getItem('token'),
      }
    })
      .then(response => {
        if (!response.ok) {
          throw new Error('Network response was not ok');
        }
        loadNotifications();
      })
      .catch(error => {
        alert(error)
      });
  }

  function handleMarkAllNotificationsAsReadButton() {
    fetch(apiUrl + '/read?userId=' + localStorage.getItem('userId'), {
      method: 'PUT',
      headers: {
        "Authorization": `Bearer ` + localStorage.getItem('token'),
      }
    })
      .then(response => {
        if (!response.ok) {
          throw new Error('Network response was not ok');
        }
        loadNotifications();
      })
      .catch(error => {
        alert(error)
      });
  }

  const listOfNotifications = data.map((notification) =>
    <tr key={notification.id}>
      <th scope="row">{notification.id}</th>
      <td>{notification.name}</td>
      <td>{notification.taskName}</td>
      <td>{notification.createDate.toString()}</td>
      <td>{notification.read ? "Yes" : <b>No</b>}</td>
      <td>{notification.readDate && notification.readDate.toString()}</td>
      <td>{notification.user.name} {notification.user.surname}</td>
      <td><Link to='/details' state={{ id: notification.task.id }}>Go to task</Link></td>

      <td>
        <button type="button" className="btn btn-primary" disabled={notification.read} onClick={() => handleMarkNotificationAsRead(notification.id)}>Read</button>
      </td>
      <td>
        <button type="button" className="btn btn-danger" onClick={() => handleRemove(notification.id)}>Remove</button>
      </td>
    </tr>
  );

  return <div>
    <table className="table">
      <thead>
        <tr>
          <th scope="col">#</th>
          <th scope="col">Name</th>
          <th scope="col">Task name</th>
          <th scope="col">Create date</th>
          <th scope="col">Is read</th>
          <th scope="col">Read date</th>
          <th scope="col">User assigned</th>
          <th scope="col">Link</th>
          <th scope="col">Mark as read</th>
          <th scope="col">Remove</th>
        </tr>
      </thead>
      <tbody>
        {listOfNotifications}
      </tbody>
    </table>

    <div className="d-flex align-items-center justify-content-center">
      <button className='btn btn-primary' onClick={() => handleMarkAllNotificationsAsReadButton()}>Mark all as read</button>
    </div>
  </div>
}

export default Notifications;
