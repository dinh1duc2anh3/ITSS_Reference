import axios from "axios";
const apiURL = process.env.REACT_APP_API_URL;

export const getUserById = async (uId) => {
  try {
    let res = await axios.post(`${apiURL}/api/user/signle-user`, { uId });
    return res.data;
  } catch (error) {
    console.log(error);
  }
};

export const updatePersonalInformationFetch = async (userData) => {
  try {
    let res = await axios.post(`${apiURL}/api/user/edit-user`, userData);
    return res.data;
  } catch (error) {
    console.log(error);
  }
};

export const getOrderByUser = async (uId) => {
  try {
    // Use the available backend endpoint for order history
    let res = await axios.get(`${apiURL}/api/v1/order/history/${uId}`, {
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${localStorage.getItem("jwt") ? JSON.parse(localStorage.getItem("jwt")).token : ""}`
      }
    });
    return {
      success: true,
      Order: res.data
    };
  } catch (error) {
    console.log(error);
  }
};

export const updatePassword = async (formData) => {
  try {
    let res = await axios.post(`${apiURL}/api/user/change-password`, formData);
    return res.data;
  } catch (error) {
    console.log(error);
    throw error;
  }
};

export const cancelOrder = async (orderId) => {
  try {
    // Use the available backend endpoint for order cancellation
    let res = await axios.put(`${apiURL}/api/v1/order/cancel/${orderId}`, {}, {
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${localStorage.getItem("jwt") ? JSON.parse(localStorage.getItem("jwt")).token : ""}`
      }
    });
    return res.data;
  } catch (error) {
    console.log("Error cancelling order:", error);
    throw error;
  }
}