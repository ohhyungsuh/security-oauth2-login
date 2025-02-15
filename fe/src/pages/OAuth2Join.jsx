import { useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import { useDispatch, useSelector } from 'react-redux';
import { login } from "../redux/slices/authSlice";

export default function OAuth2Join() {
    const [birth, setBirth] = useState("");
    // const { accessToken, refreshToken } = useSelector((state) => state.auth);

    const accessToken = localStorage.getItem("access");
    const refreshToken = localStorage.getItem("refresh");
    
    const navigate = useNavigate();
    const dispatch = useDispatch();
    
    const handleOAuth2SignUp = async (e) => {
        e.preventDefault();

        axios({
            method: "put",
            url: "http://localhost:8080/api/v1/users/oauth/join",
            data: { birth },
            headers: {
                "access": `${accessToken}`,
                "refresh": `${refreshToken}`
            }
        })
        .then(response => {
            console.log("oauth2 추가 정보 입력 성공", response);
            console.log("user", response.data);
            dispatch(login({ user: response.data }));
            navigate("/");
        })
        .catch(error => {
            console.log(error);
        })
    }

    return (
        <form onSubmit={handleOAuth2SignUp}>
            <input type="date" placeholder="생년월일" value={birth} onChange={(e) => setBirth(e.target.value)} />

            <button type="submit">추가 정보 입력 완료</button>
        </form>
    )
}