import React, { useState } from 'react';
import logo from '../logo.svg';
import { useNavigate } from 'react-router-dom';

function Register(props) {
  const [user, setUser] = useState({})

  const apiUrl = 'http://localhost:8080/auth/';

  const navigate = useNavigate()

  function register(event) {
    event.preventDefault();

    fetch(apiUrl + 'register', {
      method: 'POST',
      body: JSON.stringify(user),
      headers: {
        "Content-type": "application/json; charset=UTF-8",
      }
    })
      .then(response => {
        if (!response.ok) {
          throw new Error('Network response was not ok');
        }
        return response.json();
      })
      .then(data => {
        navigate('/login')
      })
      .catch(error => {
        alert(error)
      });
  }

  function handleChange(event) {
    setUser({
      ...user,
      [event.target.id]: event.target.value
    })
  }

  return <div>
    <img src={logo} className="App-logo" alt="logo" />
    <form onSubmit={register}>
      <div className="form-group">
        <label for="email">Email</label>
        <input type="email" className="form-control" id="email" value={user.email} placeholder="Enter email" onChange={handleChange} />
      </div>
      <div className="form-group">
        <label for="username">Username</label>
        <input className="form-control" id="username" value={user.username} placeholder="Enter username" onChange={handleChange} />
      </div>
      <div className="form-group">
        <label for="password">Password</label>
        <input type="password" className="form-control" id="password" value={user.password} placeholder="Password" onChange={handleChange} />
      </div>
      <div className="form-group">
        <label for="name">Name</label>
        <input className="form-control" id="name" value={user.name} placeholder="Enter name" onChange={handleChange} />
      </div>
      <div className="form-group">
        <label for="surname">Surname</label>
        <input className="form-control" id="surname" value={user.surname} placeholder="Enter surname" onChange={handleChange} />
      </div>
      <button type="submit" className="btn btn-primary">Register</button>
    </form>
  </div>
}

export default Register;
