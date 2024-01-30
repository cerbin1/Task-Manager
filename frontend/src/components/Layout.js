import { Outlet, Link } from "react-router-dom";

const Layout = () => {
  return (
    <>
      <nav class="navbar navbar-expand-lg navbar-light bg-light">
        <a class="navbar-brand" href="#">Navbar</a>
        <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarNav" aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
          <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarNav">
          <ul class="navbar-nav">
            <li class="nav-item active">
              <a class="nav-link" href="#"><Link to="/">Main Page</Link></a>
            </li>
            <li class="nav-item">
              <a class="nav-link" href="#"><Link to="/login">Login</Link></a>
            </li>
            <li class="nav-item">
              <a class="nav-link" href="#"><Link to="/register">Register</Link></a>
            </li>
            <li class="nav-item">
              <a class="nav-link" href="#"><Link to="/list">List tasks</Link></a>
            </li>
            <li class="nav-item">
              <a class="nav-link" href="#"><Link to="/create">Create task</Link></a>
            </li>
          </ul>
        </div>
      </nav>

      <Outlet />
    </>
  )
};

export default Layout;