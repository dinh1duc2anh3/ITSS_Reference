import { getAllOrders } from "./FetchApi";

export const fetchData = async (dispatch) => {
  dispatch({ type: "loading", payload: true });
  let responseData = await getAllOrders();
  setTimeout(() => {
    if (responseData && Array.isArray(responseData)) {
      dispatch({
        type: "fetchOrderAndChangeState",
        payload: responseData,
      });
      dispatch({ type: "loading", payload: false });
    }
  }, 1000);
};

/* This method call the editmodal & dispatch category context */
export const editOrderReq = (orderId, type, status, dispatch) => {
  if (type) {
    console.log("click update");
    dispatch({ type: "updateOrderModalOpen", orderId: orderId, status: status });
  }
};

/* Filter All Order */
export const filterOrder = async (
  type,
  data,
  dispatch,
  dropdown,
  setDropdown
) => {
  let status = type === "All" ? undefined : type;
  let responseData = await getAllOrders(status);
  if (responseData && Array.isArray(responseData)) {
    dispatch({
      type: "fetchOrderAndChangeState",
      payload: responseData,
    });
    setDropdown(!dropdown);
  }
};
