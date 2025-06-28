import axios from "axios";
const apiURL = process.env.REACT_APP_API_URL;
const base = `${apiURL}/api/v1/orders`;

export const placeOrder = async (orderData) => {
  try {
    let res = await axios.post(`${apiURL}/api/v1/orders/place`, orderData, {
      headers: {
        'Content-Type': 'application/json'
      }
    });
    return res.data;
  } catch (error) {
    console.log(error);
    throw error;
  }
};

// Payment for order - Updated to match backend endpoint
export const payOrder = async (orderId, paymentData) => {
  try {
    let res = await axios.post(`${apiURL}/api/v1/orders/${orderId}/pay`, paymentData, {
      headers: {
        'Content-Type': 'application/json'
      }
    });
    return res.data;
  } catch (error) {
    console.log(error);
    throw error;
  }
};

// Get order details by ID
export const getOrderDetails = async (orderId) => {
  try {
    const res = await axios.get(`${base}/${orderId}`);
    return res.data;
  } catch (error) {
    console.error("Get order details failed:", error);
    throw error;
  }
};

// Set delivery info
export const setDeliveryInfo = async (orderId, deliveryInfoDTO) => {
  try {
    const res = await axios.put(`${base}/${orderId}/delivery`, deliveryInfoDTO);
    return res.data;
  } catch (error) {
    console.error("Set delivery info failed:", error);
    throw error;
  }
};


/*
// Initiate payment
export const initiatePayment = async (orderId, paymentMethod) => {
  try {
    const res = await axios.post(`${base}/${orderId}/pay`, null, {
      params: { paymentMethod },
    });
    return res.data;
  } catch (error) {
    console.error("Payment initiation failed:", error);
    throw error;
  }
}; */


// VNPay payment - Updated to match backend endpoint
export const createVNPayPayment = async (orderId, paymentData) => {
  try {
    let res = await axios.post(`${apiURL}/api/v1/payment/${orderId}`, paymentData, {
      headers: {
        'Content-Type': 'application/json'
      }
    });
    return res.data;
  } catch (error) {
    console.log(error);
    throw error;
  }
};


// Set rush delivery info
export const setRushDeliveryInfo = async (orderId, rushInfoDTO) => {
  try {
    const res = await axios.put(`${base}/${orderId}/rush-delivery`, rushInfoDTO);
    return res.data;
  } catch (error) {
    console.error("Set rush delivery info failed:", error);
    throw error;
  }
};

// Get invoice for order
export const getInvoice = async (orderId) => {
  try {
    const res = await axios.get(`${base}/${orderId}/invoice`);
    return res.data;
  } catch (error) {
    console.error("Get invoice failed:", error);
    throw error;
  }
};

/*
// Initiate payment
export const initiatePayment = async (orderId, paymentMethod) => {
  try {
    const res = await axios.post(`${base}/${orderId}/pay`, null, {
      params: { paymentMethod },
    });
    return res.data;
  } catch (error) {
    console.log(error);
    console.error("Payment initiation failed:", error);
    throw error;
  }
}; */


// Get order history for a customer
export const getOrderHistory = async (customerId) => {
  try {
    const res = await axios.get(`${base}/history/${customerId}`);
    return res.data;
  } catch (error) {
    console.error("Get order history failed:", error);
    throw error;
  }
};