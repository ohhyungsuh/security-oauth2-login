import { login } from "../redux/slices/authSlice";
import axios from "axios";

export const handleLogin = (username, password, dispatch, navigate) => {
    axios({
        method: "post",
        url: "http://localhost:8080/login",
        data: { username, password },
        headers: { "Content-Type": "application/x-www-form-urlencoded" }
    })
    .then(response => {
        console.log(response);

        const accessToken = response.headers["access"];
        const refreshToken = response.headers["refresh"];

        // 사용자 정보 요청
        axios({
            method: "get",
            url: "http://localhost:8080/api/v1/users/me",
            headers: {
                "access": accessToken,
                "refresh": refreshToken
            }
        })
        .then(userResponse => {
            console.log("사용자 정보:", userResponse.data);

            dispatch(login({ user: userResponse.data }));

            localStorage.setItem("access", accessToken);
            localStorage.setItem("refresh", refreshToken);

            navigate("/");
        })
        .catch(error => {
            console.log("사용자 정보 요청 실패:", error);
        });

    })
    .catch(error => {
        console.log("로그인 실패:", error);
    });
};