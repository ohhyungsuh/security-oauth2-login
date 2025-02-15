import { createSlice } from "@reduxjs/toolkit"

const initialState = {
    user: null,
    isAuthenticated: false,
}

const authSlice = createSlice({
    name: "auth",
    initialState,
    reducers: {
        login: (state, action) => {
            return {
                ...state, 
                ...action.payload,
                isAuthenticated: true 
            }
        },
        logout: () => initialState,
    },
})

export const { login, logout } = authSlice.actions;
export default authSlice.reducer;