import { useState } from "react"
import { useDispatch } from "react-redux"
import { useNavigate } from "react-router-dom"
import { handleLogin } from "../utils/authUtils";


export default function SignIn() {

    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const dispatch = useDispatch();
    const navigate = useNavigate();

    const handleSubmit = (e) => {
        e.preventDefault();
        handleLogin(email, password, dispatch, navigate);
    }

    return (
        <>
            <form onSubmit={handleSubmit}>
                <h1>SignIn</h1>
                <input type="email" placeholder="이메일" value={email} onChange={(e) => setEmail(e.target.value)} />
                <input type="password" placeholder="비밀번호" value={password} onChange={(e) => setPassword(e.target.value)} />

                <button type="submit">로그인</button>
            </form>
            <button type="button" onClick={() => window.location.href = "http://localhost:8080/oauth2/authorization/google"}>구글 계정으로 로그인</button>
            <button type="button" onClick={() => navigate("/signup")}>회원가입</button>
        </>
    )
}