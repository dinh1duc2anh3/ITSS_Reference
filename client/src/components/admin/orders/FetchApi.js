import axios from "axios";
const apiURL = process.env.REACT_APP_API_URL;

// Get order by ID - Updated to match backend endpoint
export const getOrderById = async (orderId) => {
  try {
    let res = await axios.get(`${apiURL}/api/v1/order/${orderId}`);
    return res.data;
  } catch (error) {
    console.log(error);
    throw error;
  }
};

// Get all orders, optionally filtered by status
export const getAllOrders = async (status) => { 
  try {
    let url = `${apiURL}/api/v1/order/all`;
    if (status && status !== "All") {
      url += `?status=${status}`;
    }
    let res = await axios.get(url);
    return res.data;
  } catch (error) {
    console.log(error);
    throw error;
  }
}

// Update order status - Updated to match backend endpoint
export const updateOrderStatus = async (orderId, orderStatus) => { 
  try {
    let res = await axios.put(`${apiURL}/api/v1/order/${orderId}/status`, null, {
      params: { orderStatus },
      headers: {
        'Content-Type': 'application/json'
      }
    });
    return res.data;
  } catch (error) {
    console.log(error);
    throw error;
  }
}

export const deleteOrder = async (orderId) => {
  try {
    let res = await axios.put(`${apiURL}/api/v1/order/${orderId}/delete`, {}, {
      headers: {
        'Content-Type': 'application/json'
      }
    });
    return res.data;
  } catch (error) {
    console.log(error);
    throw error;
  }   
}