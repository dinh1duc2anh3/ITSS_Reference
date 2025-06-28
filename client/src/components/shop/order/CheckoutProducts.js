import React, { Fragment, useContext, useState } from "react";
import { useHistory } from "react-router-dom";
import { LayoutContext } from "../layout";
import { createOrder } from "./FetchApi";

// Placeholder for a message box/modal, replacing alert()
const MessageBox = ({ message, onClose }) => {
  if (!message) return null;
  return (
    <div className="fixed inset-0 bg-gray-600 bg-opacity-50 flex items-center justify-center z-50">
      <div className="bg-white p-6 rounded-lg shadow-xl max-w-sm w-full">
        <p className="text-lg text-gray-800 mb-4">{message}</p>
        <button
          onClick={onClose}
          className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700 transition"
        >
          OK
        </button>
      </div>
    </div>
  );
};

const apiURL = process.env.REACT_APP_API_URL;

const CheckoutProducts = () => {
  const history = useHistory();
  const { data } = useContext(LayoutContext);
  const [message, setMessage] = useState(null); // For custom message box

  // Use CartDTO structure from context
  const cart = data.cart || {};
  const products = cart.items || [];
  const total = products.reduce((sum, item) => sum + (item.product.pPrice * item.quantity), 0);

  const isLoggedIn = !!localStorage.getItem("token");
  // Function to handle message box close
  const handleMessageClose = () => {    
    setMessage(null);
  }
  // Call createOrder API with CartDTO and redirect to /order/create with orderId
  const handleCreateOrder = async () => {
    if (!isLoggedIn) {
      setMessage("Please log in to create an order.");
      history.push("/login");
      return;
    }
    if (products.length === 0) {
      setMessage("Your cart is empty. Please add products to create an order.");
      return;
    }
    try {
      const result = await createOrder(cart);
      const orderId = result.orderId;
      history.push("/user/order/create", { orderId });
      setMessage(`Order created successfully! Order ID: ${orderId}.`);
      console.log(`${orderId}`);
    } catch (err) {
      setMessage("Failed to create order: " + (err.message || err.toString()));
    }
  };

  return (
    <div className="mx-4 mt-20 md:mx-12 md:mt-32 lg:mt-24 font-inter">
      <MessageBox message={message} onClose={handleMessageClose} />

      <h2 className="text-3xl font-bold mb-6 text-gray-800">Checkout</h2>

      {/* Grouping of Cart Items and Cart Total */}
      <div className="p-6 bg-white rounded-lg shadow-md mb-8">
        <h3 className="text-xl font-semibold mb-4 text-gray-700">Your Cart Items</h3>
        <div className="grid grid-cols-1 gap-4">
          {products.length > 0 ? (
            products.map((item, index) => (
              <div
                key={index}
                className="flex items-center space-x-4 p-4 border border-gray-200 rounded-lg bg-gray-50"
              >
                {/* Product Image */}
                <img
                  onClick={() => history.push(`/products/${item.product._id}`)}
                    className="cursor-pointer md:h-20 md:w-20 object-cover object-center"
                    src={`${apiURL}/uploads/products/${item.product.pImages[0]}`}
                    alt="product"
                />
                <div className="flex-1 min-w-0">
                  <div className="text-lg font-semibold text-gray-800 truncate">
                    {item.product.pName}
                  </div>
                  <div className="text-sm text-gray-600">
                    Price: {item.product.pPrice.toLocaleString('vi-VN')}₫
                  </div>
                  <div className="text-sm text-gray-600">
                    Quantity: {item.quantity}
                  </div>
                </div>
                <div className="text-lg font-bold text-gray-700 flex-shrink-0">
                  Subtotal: {(item.product.pPrice * item.quantity).toLocaleString('vi-VN')}₫
                </div>
              </div>
            ))
          ) : (
            <div className="text-center text-gray-600 p-4">No products in your cart. Add some to proceed!</div>
          )}
        </div>
        {/* Cart Total Display */}
        <div className="mt-6 pt-4 border-t border-gray-200 flex justify-between text-xl font-bold text-gray-800">
          <span>Cart Total</span>
          <span>{total.toLocaleString('vi-VN')}₫</span>
        </div>
      </div>

      <div className="mt-6 flex justify-end">
        <button
          className="bg-blue-600 text-white px-8 py-4 rounded-xl hover:bg-blue-700 transition duration-300 ease-in-out shadow-lg text-lg font-bold"
          onClick={handleCreateOrder}
        >
          Create Order
        </button>
      </div>
      <div className="h-20"></div> {/* Spacer for bottom padding */}
    </div>
  );
};

export default CheckoutProducts;