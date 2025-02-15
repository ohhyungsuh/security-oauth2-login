import { useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";

export default function SignUp() {

    const [name, setName] = useState("");
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [birth, setBirth] = useState("");

    const navigate = useNavigate();
        
    const handleSignup = async (e) => {
        e.preventDefault();

        axios({
            method: "post",
            url: "http://localhost:8080/api/v1/users/join",
            data: { name, email, password, birth },
        })
        .then(res => {
            console.log(res);
            navigate("/");
        })
        .catch(error => {
            console.log(error);
        });
    }

    return (
        <form onSubmit={handleSignup}>
            <input type="text" placeholder="이름" value={name} onChange={(e) => setName(e.target.value)} />
            <input type="email" placeholder="이메일" value={email} onChange={(e) => setEmail(e.target.value)} />
            <input type="password" placeholder="비밀번호" value={password} onChange={(e) => setPassword(e.target.value)} />
            <input type="date" placeholder="생년월일" value={birth} onChange={(e) => setBirth(e.target.value)} />

            <button type="submit">회원가입</button>
        </form>
    )
}