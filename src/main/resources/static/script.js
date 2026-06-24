// API endpoints
const API_BASE_URL = '/api';
const ENDPOINTS = {
    USERS: `${API_BASE_URL}/user/findAll`,
    BINARY_CONTENT: `${API_BASE_URL}/binaryContent/find`
};

// Initialize the application
document.addEventListener('DOMContentLoaded', () => {
    fetchAndRenderUsers();
});

// Fetch users from the API
async function fetchAndRenderUsers() {
    try {
        const response = await fetch(ENDPOINTS.USERS);

        if (!response.ok) {
            throw new Error('Failed to fetch users');
        }

        const users = await response.json();
        renderUserList(users);
    } catch (error) {
        console.error('Error fetching users:', error);

        const userListElement = document.getElementById('userList');
        userListElement.innerHTML = '<p>사용자 목록을 불러오지 못했습니다.</p>';
    }
}

// Fetch user profile image
async function fetchUserProfile(profileId) {
    try {
        const response = await fetch(`${ENDPOINTS.BINARY_CONTENT}?binaryContentId=${profileId}`);

        if (!response.ok) {
            throw new Error('Failed to fetch profile');
        }

        const profile = await response.json();

        // profile.bytes가 있으면 base64 이미지로 변환
        if (profile && profile.contentType && profile.bytes) {
            return `data:${profile.contentType};base64,${profile.bytes}`;
        }

        return null;
    } catch (error) {
        console.error('Error fetching profile:', error);
        return null;
    }
}

// Render user list
async function renderUserList(users) {
    const userListElement = document.getElementById('userList');
    userListElement.innerHTML = '';

    if (!users || users.length === 0) {
        userListElement.innerHTML = '<p>등록된 사용자가 없습니다.</p>';
        return;
    }

    for (const user of users) {
        const userElement = document.createElement('div');
        userElement.className = 'user-item';

        const profileUrl = user.profileId
            ? await fetchUserProfile(user.profileId)
            : null;

        let profileElementHtml;

        if (profileUrl) {
            profileElementHtml = `
                <img src="${profileUrl}" alt="${user.username}" class="user-avatar">
            `;
        } else {
            const firstLetter = user.username
                ? user.username.charAt(0).toUpperCase()
                : '?';

            profileElementHtml = `
                <div class="user-avatar avatar-text">${firstLetter}</div>
            `;
        }

        userElement.innerHTML = `
            ${profileElementHtml}
            <div class="user-info">
                <div class="user-name">${user.username}</div>
                <div class="user-email">${user.email}</div>
            </div>
            <div class="status-badge ${user.online ? 'online' : 'offline'}">
                ${user.online ? '온라인' : '오프라인'}
            </div>
        `;

        userListElement.appendChild(userElement);
    }
}