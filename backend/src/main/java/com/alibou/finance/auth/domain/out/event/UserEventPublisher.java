package com.alibou.finance.auth.domain.out.event;

import com.alibou.finance.auth.domain.agregate.User;
import com.alibou.finance.shared.domain.event.UserEmailChangedEvent;
import com.alibou.finance.shared.domain.vo.user.UserId;
import com.alibou.finance.shared.domain.vo.user.Email;

public interface UserEventPublisher {
    void publish(UserEmailChangedEvent event);
}
