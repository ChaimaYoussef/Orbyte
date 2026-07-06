import './Products.css';

export default function Products() {
  // Placeholder data; replace with fetch from backend when ready
  const products = [
    { id: 1, name: 'Produit A', price: '$10' },
    { id: 2, name: 'Produit B', price: '$20' },
    { id: 3, name: 'Produit C', price: '$30' },
  ];

  return (
    <section className="products-page">
      <h1>Nos produits</h1>
      <ul>
        {products.map((p) => (
          <li key={p.id}>
            {p.name} – {p.price}
          </li>
        ))}
      </ul>
    </section>
  );
}
