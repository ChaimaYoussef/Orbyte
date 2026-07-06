import './NavBar.css';

export default function NavBar({ setPage, currentPage }) {
  const navItems = [
    { name: 'Home', page: 'home' },
    { name: 'Products', page: 'products' },
    { name: 'About', page: 'about' },
  ];

  return (
    <nav className="navbar">
      {navItems.map((item) => (
        <button
          key={item.page}
          className={currentPage === item.page ? 'active' : ''}
          onClick={() => setPage(item.page)}
        >
          {item.name}
        </button>
      ))}
    </nav>
  );
}
