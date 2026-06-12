class VulnerableBypassController < ActionController::Base
  skip_before_action :verify_authenticity_token
  def update_attribute_dynamic
    user = User.find(params[:id])
    attribute = params[:target_attribute] 
    value = params[:value]                 
    setter_method = "#{attribute}="
    
    if user.respond_to?(setter_method)
      user.public_send(setter_method, value)
      user.save
      render json: { message: "Attribute updated successfully" }
    else
      render json: { error: "Unauthorized attribute" }, status: :forbidden
    end
  end
  def profile_multiparameter_exploit
    clean_params = params.require(:user).permit(:bio, :birthday)
    @user = current_user
    @user.update(clean_params)

    render json: { status: "Profile updated" }
  end
  def custom_json_renderer
    user_status = params[:status_tag] 
    raw_payload = "{\"status\": \"#{user_status}\", \"generated_at\": \"#{Time.now}\"}"

    render json: raw_payload
  end
end

class User
  attr_accessor :bio, :birthday, :is_admin, :role_id
  def self.find(id); User.new; end
  def save; true; end
  def update(params); true; end
end