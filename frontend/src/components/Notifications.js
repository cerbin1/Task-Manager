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

  const listOfNotifications = data.map((notification) =>
    <tr key={notification.id}>
      <th scope="row">{notification.id}</th>
      <td>{notification.name}</td>
      <td>{notification.createDate.toString()}</td>
      <td>{notification.read}</td>
      <td>{notification.readDate.toString()}</td>
      <td>{notification.user.name} {notification.user.surname}</td>
    </tr>
  );

  return <div>
    <table className="table">
      <thead>
        <tr>
          <th scope="col">#</th>
          <th scope="col">Name</th>
          <th scope="col">Create date</th>
          <th scope="col">Is read</th>
          <th scope="col">Read date</th>
          <th scope="col">User assigned</th>
        </tr>
      </thead>
      <tbody>
        {listOfNotifications}
      </tbody>
    </table>
  </div>
}

export default Notifications;
