package sa.elect;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import sa.elect.service.projection.ElectionUser;

import java.util.Collection;
import java.util.List;

public class SystemUser extends ElectionUser implements UserDetails {


	public SystemUser(ElectionUser u) {
		super(u.id, u.first, u.last, u.role, u.password, u.studentId);
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of(new SimpleGrantedAuthority(role.toString()));
	}

	@Override
	public String getUsername() {
		return super.getStudentId();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
}
