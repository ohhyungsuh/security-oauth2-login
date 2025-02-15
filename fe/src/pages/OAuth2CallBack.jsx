import { useEffect } from "react";
import { useDispatch } from "react-redux";
import { login } from "../redux/slices/authSlice";
import axios from "axios";
import { useNavigate } from "react-router-dom";

export default function OAuth2CallBack() {
    const dispatch = useDispatch();
    const navigate = useNavigate();

    useEffect(() => {
        const getTokensAndRedirect = async () => {
            try {
                const response = await axios({
                    method: "get",
                    url: "http://localhost:8080/endpoint",
                    withCredentials: true,
                });

                console.log("OAuth2 성공, 응답 확인 중")
                console.log("response:", response);

                const accessToken = response.headers["access"];
                const refreshToken = response.headers["refresh"];  

                if(!accessToken || !refreshToken) {
                    console.error("토큰이 존재하지 않습니다.");
                    return;
                }

                localStorage.setItem("access", accessToken);
                localStorage.setItem("refresh", refreshToken);

                if(response.status === 201) {
                    navigate("/oauth/join");
                } else if(response.status === 200) {
                    console.log("user", response.data);
                    dispatch(login({ user: response.data }));
                    navigate("/");
                }
                
            } catch (error) {
                console.error("OAuth2 에러:", error);
            }
        }

        getTokensAndRedirect();
    }, [dispatch, navigate]);

    return <h2>소셜 로그인 처리 중...</h2>; 
}