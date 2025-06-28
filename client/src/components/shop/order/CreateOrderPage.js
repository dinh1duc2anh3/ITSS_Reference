import React, { useState, useEffect, useContext } from "react";
import { useHistory, useLocation } from "react-router-dom";
import { LayoutContext } from "../layout";
import { placeOrder, setDeliveryInfo, setRushDeliveryInfo } from "./FetchApi";

// Re-using MessageBox pattern for alerts instead of browser's alert()
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

const CreateOrderPage = () => {
  const history = useHistory();
  const location = useLocation();
  const { data } = useContext(LayoutContext);

  useEffect(() => {
    const isLoggedIn = !!localStorage.getItem("token");
    if (!isLoggedIn) {
      history.replace("/login");
    }
  }, [history]);

  // Get orderId from location.state (passed from CheckoutProducts component)
  const orderId = location.state?.orderId || null;

  // Cart data from LayoutContext (this is the initial cart from checkout)
  const initialCartItems = data.cart?.items || [];
  const initialCartSubtotal = data.cart?.total || 0;

  const [recipientName, setRecipientName] = useState("");
  const [phoneNumber, setPhoneNumber] = useState("");
  const [email, setEmail] = useState("");
  const [provinceCity, setProvinceCity] = useState("");
  const [address, setAddress] = useState("");
  const [deliveryInstructions, setDeliveryInstructions] = useState("");
  const [deliveryInfo, setDeliveryInfoState] = useState(null);
  const [showDeliveryForm, setShowDeliveryForm] = useState(false);

  const [isRushOrder, setIsRushOrder] = useState(false);
  const [showRushForm, setShowRushForm] = useState(false);
  const [rushDeliveryTime, setRushDeliveryTime] = useState("");
  const [rushDeliveryInstructions, setRushDeliveryInstructions] = useState("");
  const [rushDeliveryInfo, setRushDeliveryInfoState] = useState(null);

  const [isOrderConfirmed, setIsOrderConfirmed] = useState(false);

  // States to store the split order DTOs from the backend
  const [standardOrderData, setStandardOrderData] = useState(null);
  const [rushOrderData, setRushOrderData] = useState(null);

  // Combined totals for display at the bottom (these will be updated after placeOrder)
  const [combinedShippingFee, setCombinedShippingFee] = useState(0);
  const [combinedTotal, setCombinedTotal] = useState(initialCartSubtotal); // Initialize with cart subtotal

  const discount = 0; // Fixed discount placeholder

  // State for MessageBox alerts
  const [alertMessage, setAlertMessage] = useState(null);
  const showAlert = (message) => setAlertMessage(message);

  // Generic handler to reset isOrderConfirmed if any relevant input changes
  const handleInputChange = (setterFunction) => (e) => {
    setterFunction(e.target.value);
    if (isOrderConfirmed) {
      setIsOrderConfirmed(false);
    }
  };

  // Set delivery info
  const handleDeliveryInfoSubmit = async (e) => {
    e.preventDefault();
    if (!orderId) {
      showAlert("Order not created yet. Please go back to checkout.");
      return;
    }
    const deliveryInfoDTO = {
      recipientName,
      phoneNumber,
      email,
      provinceCity,
      address,
      deliveryInstructions,
    };
    try {
      await setDeliveryInfo(orderId, deliveryInfoDTO);
      setDeliveryInfoState(deliveryInfoDTO);
      setShowDeliveryForm(false);
      showAlert("Delivery info saved!");
    } catch (err) {
      showAlert("Failed to save delivery info: " + (err?.response?.data?.message || err.message));
    }
  };

  // Rush order checkbox
  const handleRushOrderChange = (e) => {
    const checked = e.target.checked;
    setIsRushOrder(checked);
    if (isOrderConfirmed) {
      setIsOrderConfirmed(false); // Reset if rush order status changes
    }
    if (checked) {
      setShowRushForm(true);
    } else {
      setShowRushForm(false);
      setRushDeliveryInfoState(null); // Clear rush info if unchecked
    }
  };

  // Set rush delivery info
  const handleRushDeliveryInfoSubmit = async (e) => {
    e.preventDefault();
    if (!orderId) {
      showAlert("Order not created yet. Please go back to checkout.");
      return;
    }
    try {
      const rushInfoDTO = {
        rushDeliveryTime,
        deliveryInstruction: rushDeliveryInstructions,
      };
      await setRushDeliveryInfo(orderId, rushInfoDTO);
      setRushDeliveryInfoState(rushInfoDTO);
      setShowRushForm(false);
      showAlert("Rush delivery info saved!");
    } catch (err) {
      showAlert("Failed to save rush delivery info: " + (err?.response?.data?.message || err.message));
    }
  };

  // (placeOrder API)
  const handlePlaceOrder = async (e) => {
    e.preventDefault();
    if (!deliveryInfo) {
      showAlert("Please enter delivery info first.");
      return;
    }
    if (isRushOrder && !rushDeliveryInfo) {
      showAlert("Please enter rush delivery info.");
      return;
    }
    try {
      const orderDataForBackend = {
        orderId,
        isRushOrder,
        deliveryInfo,
        ...(isRushOrder && { rushDeliveryInfo }),
      };

      const result = await placeOrder(orderDataForBackend); // This returns SplitOrderDTO

      let currentCombinedShippingFee = 0;
      let currentCombinedTotal = 0;
      let currentStandardOrderData = null;
      let currentRushOrderData = null;

      if (result.standardOrder) {
        currentStandardOrderData = result.standardOrder;
        currentCombinedShippingFee += result.standardOrder.shippingFee || 0;
        currentCombinedTotal += result.standardOrder.total || 0;
      }
      if (result.rushOrder) {
        currentRushOrderData = result.rushOrder;
        currentCombinedShippingFee += result.rushOrder.shippingFee || 0;
        currentCombinedTotal += result.rushOrder.total || 0;
      }

      setStandardOrderData(currentStandardOrderData);
      setRushOrderData(currentRushOrderData);
      setCombinedShippingFee(currentCombinedShippingFee);
      setCombinedTotal(currentCombinedTotal);

      setIsOrderConfirmed(true);
      showAlert("Order placed successfully! You can now proceed to payment.");
      history.push("/user/orders"); // Navigate to user orders or payment page
    } catch (error) {
      const errorMessage = error?.response?.data?.message || error.message || "An unexpected error occurred.";
      showAlert("Failed to place order: " + errorMessage);
    }
  };

  // Handles navigation to PayOrder (delegates to /user/orders or a dedicated payment page)
  const handleGoToPayOrder = () => {
    history.push("/user/order/pay");
  };

  // Handles Cancel Order button click, navigates back to checkout
  const handleCancelOrder = () => {
    history.push("/checkout");
  };

  // Reset calculated totals and order data if order is not confirmed,
  // or if initialCartSubtotal changes (though it shouldn't once on this page)
  useEffect(() => {
    if (!isOrderConfirmed) {
      setCombinedShippingFee(0); // Adjusted from setCalculatedShippingFee
      setCombinedTotal(initialCartSubtotal); // Adjusted from setFinalTotal
      setStandardOrderData(null);
      setRushOrderData(null);
    }
  }, [initialCartSubtotal, isOrderConfirmed]);


  // Helper function to render order item list
  const renderOrderItems = (itemsToRender) => (
    <ul className="list-disc ml-6 mt-2 text-gray-600">
      {itemsToRender.length > 0 ? (
        itemsToRender.map((item, idx) => (
          <li key={idx}>
            {item.product.pName} - {item.product.pPrice.toLocaleString('vi-VN')}₫ x {item.quantity} = {(item.product.pPrice * item.quantity).toLocaleString('vi-VN')}₫
          </li>
        ))
      ) : (
        <li>No items in this order segment.</li>
      )}
    </ul>
  );


  return (
    <div className="mx-4 mt-20 md:mx-12 md:mt-32 lg:mt-24 max-w-2xl font-inter">
      <MessageBox message={alertMessage} onClose={() => setAlertMessage(null)} />

      <h2 className="text-3xl font-bold mb-6 text-gray-800">Create New Order</h2>

      {/* Order Summary Section */}
      <div className="p-6 bg-white rounded-lg shadow-md mb-8">
        <h3 className="text-xl font-semibold mb-4 text-gray-700">1. Order Summary</h3>

        {/* Display Standard Order Summary if available */}
        {standardOrderData && (
          <div className="border-b border-gray-200 pb-4 mb-4">
            <h4 className="font-bold text-gray-800 mb-2">Standard Order Details (ID: {standardOrderData.orderId})</h4>
            <div className="mb-2">
                <strong className="text-gray-800">Items:</strong>
                {renderOrderItems(standardOrderData.items)}
            </div>
            <div className="text-right space-y-1 text-gray-700 text-sm">
                <div>
                    <strong className="font-semibold">Subtotal:</strong> <span className="ml-2">{standardOrderData.subtotal.toLocaleString('vi-VN')}₫</span>
                </div>
                <div>
                    <strong className="font-semibold">Shipping Fee:</strong> <span className="ml-2">{standardOrderData.shippingFee.toLocaleString('vi-VN')}₫</span>
                </div>
                <div className="font-bold text-base">
                    Total: <span className="ml-2">{standardOrderData.total.toLocaleString('vi-VN')}₫</span>
                </div>
            </div>
          </div>
        )}

        {/* Display Rush Order Summary if available */}
        {rushOrderData && (
          <div className="border-b border-gray-200 pb-4 mb-4">
            <h4 className="font-bold text-red-700 mb-2">Rush Order Details (ID: {rushOrderData.orderId})</h4>
            <div className="mb-2">
                <strong className="text-gray-800">Items:</strong>
                {renderOrderItems(rushOrderData.items)}
            </div>
            <div className="text-right space-y-1 text-gray-700 text-sm">
                <div>
                    <strong className="font-semibold">Subtotal:</strong> <span className="ml-2">{rushOrderData.subtotal.toLocaleString('vi-VN')}₫</span>
                </div>
                <div>
                    <strong className="font-semibold">Shipping Fee:</strong> <span className="ml-2">{rushOrderData.shippingFee.toLocaleString('vi-VN')}₫</span>
                </div>
                <div className="font-bold text-base text-red-700">
                    Total: <span className="ml-2">{rushOrderData.total.toLocaleString('vi-VN')}₫</span>
                </div>
            </div>
          </div>
        )}

        {/* Display initial cart items and overall total if no order has been confirmed yet */}
        {!standardOrderData && !rushOrderData && (
            <div className="pb-4 mb-4">
                <h4 className="font-bold text-gray-800 mb-2">Initial Cart Items</h4>
                <div className="mb-2">
                    <strong className="text-gray-800">Items:</strong>
                    {renderOrderItems(initialCartItems)}
                </div>
                <div className="text-right space-y-1 text-gray-700 text-sm">
                    <div>
                        <strong className="font-semibold">Subtotal:</strong> <span className="ml-2">{initialCartSubtotal.toLocaleString('vi-VN')}₫</span>
                    </div>
                    {/* Placeholder for shipping fee if not yet calculated by backend */}
                    <div>
                        <strong className="font-semibold">Shipping Fee:</strong> <span className="ml-2">0₫</span>
                    </div>
                    <div className="font-bold text-base">
                        Total: <span className="ml-2">{initialCartSubtotal.toLocaleString('vi-VN')}₫</span>
                    </div>
                </div>
            </div>
        )}


        {/* Grand Total section - Only show after order is confirmed to reflect backend values */}
        {isOrderConfirmed && (
          <div className="mt-4 pt-4 border-t-2 border-gray-300">
            <div className="text-right space-y-2 text-gray-700">
                <div>
                    <strong className="font-semibold">Combined Shipping Fee:</strong> <span className="ml-2">{combinedShippingFee.toLocaleString('vi-VN')}₫</span>
                </div>
                <div>
                    <strong className="font-semibold">Discount:</strong> <span className="ml-2">{discount.toLocaleString('vi-VN')}₫</span>
                </div>
                <div className="text-2xl font-bold text-blue-700 mt-4 pt-2 border-t border-gray-200">
                    Grand Total: <span className="ml-2">{combinedTotal.toLocaleString('vi-VN')}₫</span>
                </div>
            </div>
          </div>
        )}
      </div>

      {/* Delivery Information */}
      <div className="p-6 bg-white rounded-lg shadow-md mb-8">
        <h3 className="text-xl font-semibold mb-4 text-gray-700">2. Delivery Information</h3>
        {!deliveryInfo && !showDeliveryForm && (
          <button
            className="bg-blue-600 text-white px-6 py-3 rounded-xl hover:bg-blue-700 transition duration-300 ease-in-out shadow-lg"
            onClick={() => setShowDeliveryForm(true)}
          >
            Enter Delivery Info
          </button>
        )}

        {showDeliveryForm && (
          <form onSubmit={handleDeliveryInfoSubmit} className="space-y-4">
            <div>
              <label className="block mb-1 font-semibold text-gray-700">Recipient Name</label>
              <input
                type="text"
                className="w-full border border-gray-300 px-4 py-2 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                value={recipientName}
                onChange={handleInputChange(setRecipientName)}
                required
              />
            </div>
            <div>
              <label className="block mb-1 font-semibold text-gray-700">Phone Number</label>
              <input
                type="text"
                className="w-full border border-gray-300 px-4 py-2 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                value={phoneNumber}
                onChange={handleInputChange(setPhoneNumber)}
                required
              />
            </div>
            <div>
              <label className="block mb-1 font-semibold text-gray-700">Email</label>
              <input
                type="email"
                className="w-full border border-gray-300 px-4 py-2 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                value={email}
                onChange={handleInputChange(setEmail)}
                required
              />
            </div>
            <div>
              <label className="block mb-1 font-semibold text-gray-700">Province/City</label>
              <input
                type="text"
                className="w-full border border-gray-300 px-4 py-2 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                value={provinceCity}
                onChange={handleInputChange(setProvinceCity)}
                required
              />
            </div>
            <div>
              <label className="block mb-1 font-semibold text-gray-700">Address</label>
              <input
                type="text"
                className="w-full border border-gray-300 px-4 py-2 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                value={address}
                onChange={handleInputChange(setAddress)}
                required
              />
            </div>
            <div>
              <label className="block mb-1 font-semibold text-gray-700">Delivery Instructions (Optional)</label>
              <textarea
                className="w-full border border-gray-300 px-4 py-2 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                value={deliveryInstructions}
                onChange={handleInputChange(setDeliveryInstructions)}
                rows="3"
              ></textarea>
            </div>
            <button
              type="submit"
              className="bg-green-600 text-white px-6 py-3 rounded-xl hover:bg-green-700 transition duration-300 ease-in-out shadow-lg"
            >
              Confirm Delivery Info
            </button>
          </form>
        )}

        {deliveryInfo && (
          <div className="p-4 border border-gray-200 rounded-lg bg-gray-50 mt-4">
            <h4 className="font-bold text-gray-700 mb-2">Confirmed Delivery Details:</h4>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-x-4 gap-y-2 text-gray-600">
                <div><strong>Recipient:</strong> {deliveryInfo.recipientName}</div>
                <div><strong>Phone:</strong> {deliveryInfo.phoneNumber}</div>
                <div><strong>Email:</strong> {deliveryInfo.email}</div>
                <div><strong>Province/City:</strong> {deliveryInfo.provinceCity}</div>
                <div className="col-span-1 md:col-span-2"><strong>Address:</strong> {deliveryInfo.address}</div>
                <div className="col-span-1 md:col-span-2"><strong>Instructions:</strong> {deliveryInfo.deliveryInstructions || 'N/A'}</div>
            </div>
          </div>
        )}
      </div>

      {/* Rush Order Option */}
      <div className="p-6 bg-white rounded-lg shadow-md mb-8">
        <h3 className="text-xl font-semibold mb-4 text-gray-700">3. Rush Order Option</h3>
        <div className="mb-4">
          <label className="inline-flex items-center cursor-pointer">
            <input
              type="checkbox"
              className="form-checkbox h-5 w-5 text-blue-600 rounded"
              checked={isRushOrder}
              onChange={handleRushOrderChange}
            />
            <span className="ml-2 text-lg text-gray-800">Enable Rush Delivery</span>
          </label>
        </div>

        {showRushForm && (
          <form onSubmit={handleRushDeliveryInfoSubmit} className="space-y-4">
            <div>
              <label className="block mb-1 font-semibold text-gray-700">Preferred Rush Delivery Time</label>
              <input
                type="datetime-local"
                className="w-full border border-gray-300 px-4 py-2 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                value={rushDeliveryTime}
                onChange={handleInputChange(setRushDeliveryTime)}
                required
              />
            </div>
            <div>
              <label className="block mb-1 font-semibold text-gray-700">Rush Delivery Special Instructions (Optional)</label>
              <textarea
                className="w-full border border-gray-300 px-4 py-2 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                value={rushDeliveryInstructions}
                onChange={handleInputChange(setRushDeliveryInstructions)}
                rows="3"
              ></textarea>
            </div>
            <button type="submit" className="bg-purple-600 text-white px-6 py-3 rounded-xl hover:bg-purple-700 transition duration-300 ease-in-out shadow-lg">
              Confirm Rush Delivery Info
            </button>
          </form>
        )}

        {rushDeliveryInfo && (
          <div className="p-4 border border-gray-200 rounded-lg bg-gray-50 mt-4">
              <h4 className="font-bold text-gray-700 mb-2">Confirmed Rush Delivery Details:</h4>
              <p className="text-gray-600"><strong>Time:</strong> {new Date(rushDeliveryInfo.rushDeliveryTime).toLocaleString()}</p>
              <p className="text-gray-600"><strong>Instructions:</strong> {rushDeliveryInfo.deliveryInstruction || 'N/A'}</p>
          </div>
        )}
      </div>

      {/* Action Buttons */}
      <div className="flex justify-between items-center mt-6">
        <button
            className="bg-red-600 text-white px-6 py-3 rounded-xl hover:bg-red-700 transition duration-300 ease-in-out shadow-lg text-lg font-bold"
            onClick={handleCancelOrder}
        >
            Cancel Order
        </button>

        {!isOrderConfirmed ? (
          <button
            className="bg-green-600 text-white px-8 py-4 rounded-xl hover:bg-green-700 transition duration-300 ease-in-out shadow-lg text-lg font-bold
                         disabled:bg-gray-400 disabled:cursor-not-allowed"
            onClick={handlePlaceOrder}
            disabled={!deliveryInfo || (isRushOrder && !rushDeliveryInfo)}
          >
            Place Order
          </button>
        ) : (
          <button
            className="bg-blue-600 text-white px-8 py-4 rounded-xl hover:bg-blue-700 transition duration-300 ease-in-out shadow-lg text-lg font-bold"
            onClick={handleGoToPayOrder}
          >
            Pay Order
          </button>
        )}
      </div>
      <div className="h-20"></div> {/* Spacer for bottom padding */}
    </div>
  );
};

export default CreateOrderPage;