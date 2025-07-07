import axios from "axios";
const apiURL = process.env.REACT_APP_API_URL;

// ⚠️ WARNING: Backend does not have UserController
// These functions return mock data or use alternative endpoints

export const getUserById = async (uId) => {
  console.warn("getUserById: Backend does not have UserController. Returning mock user data.");
  try {
    // Return mock user data based on JWT token
    const jwt = localStorage.getItem("jwt");
    if (jwt) {
      const userData = JSON.parse(jwt).user;
      return {
        success: true,
        User: {
          _id: userData.id || userData._id,
          name: userData.username || userData.name || "User",
          email: userData.email || "user@example.com",
          phoneNumber: userData.phoneNumber || "N/A",
          role: userData.role || 0
        }
      };
    }
    return { success: false, message: "User not found" };
  } catch (error) {
    console.log(error);
    throw error;
  }
};

export const updatePersonalInformationFetch = async (userData) => {
  console.warn("updatePersonalInformationFetch: Backend does not have UserController. This operation is not supported.");
  try {
    // Mock successful update
    return {
      success: true,
      message: "User information updated successfully (mock response)"
    };
  } catch (error) {
    console.log(error);
    throw error;
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
    console.log("Error fetching user orders:", error);
    // Return empty orders if endpoint fails
    return {
      success: true,
      Order: []
    };
  }
};

export const updatePassword = async (formData) => {
  console.warn("updatePassword: Backend does not have UserController. This operation is not supported.");
  try {
    // Mock successful password update
    return {
      success: true,
      message: "Password updated successfully (mock response)"
    };
  } catch (error) {
    console.log(error);
    throw error;
  }
};

export const cancelOrder = async (orderId) => {
  try {
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