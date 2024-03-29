import React, { useState, useEffect } from 'react';
import Chart from 'chart.js/auto';

function Stats() {
  const [statistics, setStatistics] = useState();

  const apiUrl = 'http://localhost:8080/api/stats';

  useEffect(() => {
    fetch(apiUrl + '/tasksCount', {
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
        loadNumberOfTasksChart(data);
      })
      .catch(error => {
        alert(error)
      });

    fetch(apiUrl + '/userLoggedTime', {
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
        loadMinutesLoggedByUsersChart(data);
      })
      .catch(error => {
        alert(error)
      });

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
        setStatistics(data);
      })
      .catch(error => {
        alert(error)
      });

  }, []);

  function loadNumberOfTasksChart(data) {
    const ctx = document.getElementById('numberOfTasksChart');

    if (window.numberOfTasksChart && typeof window.numberOfTasksChart.destroy === 'function') {
      window.numberOfTasksChart.destroy();
    }

    window.numberOfTasksChart = new Chart(ctx, {
      type: 'bar',
      data: {
        labels: data.map(row => row.date),
        datasets: [{
          label: '# of tasks',
          data: data.map(row => row.count),
          borderWidth: 1
        }]
      },
      options: {
        scales: {
          y: {
            beginAtZero: true
          }
        }
      }
    });
  }

  function loadMinutesLoggedByUsersChart(data) {
    const ctx = document.getElementById('minutesLoggedByUsersChart');

    if (window.minutesLoggedByUsersChart && typeof window.minutesLoggedByUsersChart.destroy === 'function') {
      window.minutesLoggedByUsersChart.destroy();
    }

    window.minutesLoggedByUsersChart = new Chart(ctx, {
      type: 'bar',
      data: {
        labels: data.map(row => row.name),
        datasets: [{
          label: '# minutes worked',
          data: data.map(row => row.minutesLogged),
          borderWidth: 1
        }]
      },
      options: {
        scales: {
          y: {
            beginAtZero: true
          }
        }
      }
    });
  }

  return <div>
    <h2>General statistics</h2>
    {statistics && <div>
      <div className="card">
        <div className="card-body">
          Number of users:
          <span className="fw-bold px-1">{statistics.usersCount}</span>
        </div>
      </div>
      <div className="card">
        <div className="card-body">
          Number of tasks created:
          <span className="fw-bold px-1">{statistics.tasksCreated}</span>
        </div>
      </div>
      <div className="card">
        <div className="card-body">
          Number of tasks completed:
          <span className="fw-bold px-1">{statistics.tasksCompleted}</span>
        </div>
      </div>
      <div className="card">
        <div className="card-body">
          Number of subtasks:
          <span className="fw-bold px-1">{statistics.subtasksCount}</span>
        </div>
      </div>
      <div className="card">
        <div className="card-body">
          Number of notifications:
          <span className="fw-bold px-1">{statistics.notificationsCount}</span>
        </div>
      </div>
    </div>}
    <h2></h2>
    <h2>Number of tasks per day by deadline</h2>
    <canvas id="numberOfTasksChart"></canvas>
    <h2>Number of minutes logged by user</h2>
    <canvas id="minutesLoggedByUsersChart"></canvas>
  </div>
}

export default Stats;
