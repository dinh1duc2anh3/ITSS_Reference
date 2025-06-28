import React, { Fragment, useEffect, useContext, useState } from "react";
import moment from "moment";
// Ensure cancelOrder is imported and aliased to avoid name collision if needed
import { fetchOrderByUser, handleCancelOrder as cancelOrderAction } from "./Action";
import Layout, { DashboardUserContext } from "./Layout";

const apiURL = process.env.REACT_APP_API_URL;

const TableHeader = () => {
  return (
    <Fragment>
      <thead>
        <tr>
          <th className="px-4 py-2 border">Products</th>
          <th className="px-4 py-2 border">Status</th>
          <th className="px-4 py-2 border">Total</th>
          <th className="px-4 py-2 border">Recipient Name</th>
          <th className="px-4 py-2 border">Address</th>
          <th className="px-4 py-2 border">Created At</th>
          <th className="px-4 py-2 border">Last Updated</th>
          <th className="px-4 py-2 border">Actions</th>
        </tr>
      </thead>
    </Fragment>
  );
};

const TableBody = ({ order, onOrderClick, onCancelOrder }) => {
  return (
    <Fragment>
      <tr className="border-b cursor-pointer hover:bg-gray-100" onClick={() => onOrderClick(order)}>
        <td className="w-48 p-2 flex flex-col space-y-1">
          {order.items.map((item, i) => {
            const imageUrl = item.productImages && item.productImages.length > 0
              ? `${apiURL}/uploads/products/${item.productImages[0]}`
              : '/placeholder-product.jpg'; // Fallback for missing image

            return (
              <span className="block flex items-center space-x-2" key={i}>
                <img
                  className="w-8 h-8 object-cover object-center"
                  src={imageUrl}
                  alt={item.productName || 'Product Image'}
                />
                <span>{item.productName || 'N/A'}</span>
                <span>{item.quantity}x</span>
              </span>
            );
          })}
        </td>
        <td className="p-2 text-center cursor-default">
          {order.orderStatus === "DRAFT" && (
            <span className="block text-red-600 rounded-full text-center text-xs px-2 font-semibold">
              {order.orderStatus}
            </span>
          )}
          {order.orderStatus === "PENDING" && (
            <span className="block text-orange-600 rounded-full text-center text-xs px-2 font-semibold">
              {order.orderStatus}
            </span>
          )}
          {order.orderStatus === "SHIPPED" && (
            <span className="block text-blue-600 rounded-full text-center text-xs px-2 font-semibold">
              {order.orderStatus}
            </span>
          )}
          {order.orderStatus === "DELIVERED" && (
            <span className="block text-green-600 rounded-full text-center text-xs px-2 font-semibold">
              {order.orderStatus}
            </span>
          )}
          {order.orderStatus === "CANCELLED" && (
            <span className="block text-red-600 rounded-full text-center text-xs px-2 font-semibold">
              {order.orderStatus}
            </span>
          )}
        </td>
        <td className="p-2 text-center">
          {order.total ? order.total.toLocaleString('vi-VN') + ' VND' : '0 VND'}
        </td>
        <td className="p-2 text-center">
          {order.deliveryInfo ? order.deliveryInfo.recipientName : "N/A"}
        </td>
        <td className="p-2 text-center">
          {order.deliveryInfo ? `${order.deliveryInfo.address}, ${order.deliveryInfo.provinceCity}` : "N/A"}
        </td>
        <td className="p-2 text-center">
          {moment(order.createdDate).format("lll")}
        </td>
        <td className="p-2 text-center">
          {order.updatedDate ? moment(order.updatedDate).format("lll") : "N/A"}
        </td>
        <td className="p-2 text-center">
          {order.orderStatus === "PENDING" && (
            <button
              className="bg-red-500 text-white px-3 py-1 rounded hover:bg-red-600 text-xs"
              onClick={(e) => {
                e.stopPropagation();
                if (window.confirm("Are you sure you want to cancel this order?")) {
                  onCancelOrder(order.orderId);
                }
              }}
            >
              Cancel
            </button>
          )}
        </td>
      </tr>
    </Fragment>
  );
};

// Order Detail Modal Component
const OrderDetailModal = ({ order, onClose }) => {
    if (!order) return null;

    return (
        <div className="fixed inset-0 bg-gray-600 bg-opacity-50 overflow-y-auto h-full w-full z-50 flex items-center justify-center">
            <div className="relative p-5 border w-96 shadow-lg rounded-md bg-white">
                <div className="text-center">
                    <h3 className="text-lg leading-6 font-medium text-gray-900">Order Details (ID: {order.orderId})</h3>
                    <div className="mt-2 px-7 py-3">
                        <p className="text-sm text-gray-500"><strong>Status:</strong> {order.orderStatus}</p>
                        <p className="text-sm text-gray-500"><strong>Total:</strong> {order.total ? order.total.toLocaleString('vi-VN') + ' VND' : '0 VND'}</p>
                        <p className="text-sm text-gray-500"><strong>Recipient:</strong> {order.deliveryInfo ? order.deliveryInfo.recipientName : 'N/A'}</p>
                        <p className="text-sm text-gray-500"><strong>Address:</strong> {order.deliveryInfo ? `${order.deliveryInfo.address}, ${order.deliveryInfo.provinceCity}` : 'N/A'}</p>
                        <p className="text-sm text-gray-500"><strong>Created:</strong> {moment(order.createdDate).format("lll")}</p>
                        <h4 className="text-md font-medium mt-3">Items:</h4>
                        <ul className="list-disc list-inside text-sm text-gray-700">
                            {order.items.map((item, i) => (
                                <li key={i}>
                                  {item.productName || 'N/A'} (x{item.quantity}) - {item.lineTotal ? item.lineTotal.toLocaleString('vi-VN') + ' VND' : '0 VND'}
                                </li>
                            ))}
                        </ul>
                        <p className="text-sm text-gray-500 mt-2"><strong>Shipping Fee:</strong> {order.shippingFee ? order.shippingFee.toLocaleString('vi-VN') + ' VND' : '0 VND'}</p>
                        <p className="text-sm text-gray-500"><strong>Discount:</strong> ${order.discount ? order.discount.toFixed(2) : '0.00'}</p>
                        <p className="text-sm text-gray-500"><strong>Payment Status:</strong> {order.paymentStatus || 'N/A'}</p>
                        {order.rushDeliveryTime && (
                             <p className="text-sm text-gray-500"><strong>Rush Delivery Time:</strong> {moment(order.rushDeliveryTime).format("lll")}</p>
                        )}
                    </div>
                    <div className="items-center px-4 py-3">
                        <button
                            id="ok-btn"
                            className="px-4 py-2 bg-yellow-700 text-white text-base font-medium rounded-md w-full shadow-sm hover:bg-yellow-800 focus:outline-none focus:ring-2 focus:ring-yellow-500"
                            onClick={onClose}
                        >
                            Close
                        </button>
                    </div>
                </div>
            </div>
        </div>
    );
};


const OrdersComponent = () => {
  const { data, dispatch } = useContext(DashboardUserContext);
  const { OrderByUser: orders } = data;
  const [selectedOrder, setSelectedOrder] = useState(null);

  useEffect(() => {
    fetchOrderByUser(dispatch);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const handleOrderClick = (order) => {
    setSelectedOrder(order);
  };

  const handleCloseModal = () => {
    setSelectedOrder(null);
  };

  const handleCancelOrder = async (orderId) => {
    try {
      await cancelOrderAction(orderId);
      fetchOrderByUser(dispatch);
      alert(`Order ${orderId} cancelled successfully!`);
    } catch (error) {
      console.error("Error canceling order:", error);
      alert(`Failed to cancel order ${orderId}. Error: ${error.message || 'Unknown error'}`);
    }
  };

  if (data.loading) {
    return (
      <div className="w-full md:w-9/12 flex items-center justify-center py-24">
        <svg
          className="w-12 h-12 animate-spin text-gray-600"
          fill="none"
          stroke="currentColor"
          viewBox="0 0 24 24"
          xmlns="http://www.w3.org/2000/svg"
        >
          <path
            strokeLinecap="round"
            strokeLinejoin="round"
            strokeWidth="2"
            d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15"
          ></path>
        </svg>
      </div>
    );
  }
  return (
    <Fragment>
      <div className="flex flex-col w-full my-4 md:my-0 md:w-9/12 md:px-8">
        <div className="border">
          <div className="py-4 px-4 text-lg font-semibold border-t-2 border-yellow-700">
            Orders
          </div>
          <hr />
          <div className="overflow-auto bg-white shadow-lg p-4">
            <table className="table-auto border w-full my-2">
              <TableHeader />
              <tbody>
                {orders && orders.length > 0 ? (
                  orders.map((item, i) => {
                    return <TableBody key={i} order={item} onOrderClick={handleOrderClick} onCancelOrder={handleCancelOrder} />;
                  })
                ) : (
                  <tr>
                    <td
                      colSpan="8"
                      className="text-xl text-center font-semibold py-8"
                    >
                      No order found
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
            <div className="text-sm text-gray-600 mt-2">
              Total {orders && orders.length} order found
            </div>
          </div>
        </div>
      </div>

      {/* Order Detail Modal */}
      <OrderDetailModal order={selectedOrder} onClose={handleCloseModal} />
    </Fragment>
  );
};

const UserOrders = (props) => {
  return (
    <Fragment>
      <Layout children={<OrdersComponent />} />
    </Fragment>
  );
};

export default UserOrders;