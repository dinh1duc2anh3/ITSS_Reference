import { createOrder } from "./FetchApi";

export const fetchData = async (getCartByUser, dispatch) => {
  dispatch({ type: "loading", payload: true });
  try {
    // Get current user ID from localStorage
    const jwt = localStorage.getItem("jwt");
    const userId = jwt ? JSON.parse(jwt).user?.id || JSON.parse(jwt).user?._id : null;

    if (userId) {
      let responseData = await getCartByUser(userId);
      if (responseData && responseData.success && responseData.data) {
        setTimeout(function () {
          // Backend returns cart with items array
          const cartItems = responseData.data.items || [];
          dispatch({ type: "cartProduct", payload: cartItems });
          dispatch({ type: "loading", payload: false });
        }, 1000);
      } else {
        setTimeout(function () {
          dispatch({ type: "cartProduct", payload: [] });
          dispatch({ type: "loading", payload: false });
        }, 1000);
      }
    } else {
      // Fallback to localStorage cart if no user logged in
      const localCart = JSON.parse(localStorage.getItem("cart")) || [];
      setTimeout(function () {
        dispatch({ type: "cartProduct", payload: localCart });
        dispatch({ type: "loading", payload: false });
      }, 1000);
    }
  } catch (error) {
    console.log("Error fetching cart data:", error);
    // Fallback to localStorage cart on error
    const localCart = JSON.parse(localStorage.getItem("cart")) || [];
    setTimeout(function () {
      dispatch({ type: "cartProduct", payload: localCart });
      dispatch({ type: "loading", payload: false });
    }, 1000);
  }
};


export const pay = async (
  data,
  dispatch,
  state,
  setState,
  getPaymentProcess,
  totalCost,
  history
) => {
  console.log(state);
  if (!state.address) {
    setState({ ...state, error: "Please provide your address" });
  } else if (!state.phone) {
    setState({ ...state, error: "Please provide your phone number" });
  } else {
    let nonce;
    state.instance
      .requestPaymentMethod()
      .then((data) => {
        dispatch({ type: "loading", payload: true });
        nonce = data.nonce;
        let paymentData = {
          amountTotal: totalCost(),
          paymentMethod: nonce,
        };
        getPaymentProcess(paymentData)
          .then(async (res) => {
            if (res) {
              let orderData = {
                allProduct: JSON.parse(localStorage.getItem("cart")),
                user: JSON.parse(localStorage.getItem("jwt")).user._id,
                amount: res.transaction.amount,
                transactionId: res.transaction.id,
                address: state.address,
                phone: state.phone,
              };
              try {
                let resposeData = await createOrder(orderData);
                if (resposeData.success) {
                  localStorage.setItem("cart", JSON.stringify([]));
                  dispatch({ type: "cartProduct", payload: null });
                  dispatch({ type: "cartTotalCost", payload: null });
                  dispatch({ type: "orderSuccess", payload: true });
                  setState({ clientToken: "", instance: {} });
                  dispatch({ type: "loading", payload: false });
                  return history.push("/");
                } else if (resposeData.error) {
                  console.log(resposeData.error);
                }
              } catch (error) {
                console.log(error);
              }
            }
          })
          .catch((err) => {
            console.log(err);
          });
      })
      .catch((error) => {
        console.log(error);
        setState({ ...state, error: error.message });
      });
  }
};
